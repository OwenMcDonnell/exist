package org.exist.indexing.range;

import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.*;
import org.exist.dom.persistent.NodeProxy;
import org.exist.dom.persistent.NodeSet;
import org.exist.dom.persistent.NodeSetIterator;
import org.exist.indexing.lucene.LuceneUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodesFilter extends Filter {

    private final NodeSet contextSet;
    private Map<Integer, DocIdSet> cachedSets = new HashMap<Integer, DocIdSet>();

    public NodesFilter(NodeSet contextSet) {
        this.contextSet = contextSet;
    }

    @Override
    public DocIdSet getDocIdSet(LeafReaderContext context, Bits acceptDocs) throws IOException {
        DocIdSet cached = cachedSets.get(context.ord);
        return cached;
    }

    public void init(IndexReader reader) throws IOException {
        for (final LeafReaderContext context : reader.leaves()) {
            init(context);
        }
    }
    private void init(final LeafReaderContext context) throws IOException {
        final LeafReader reader = context.reader();
        final BitSet result = new FixedBitSet(reader.maxDoc());
        cachedSets.put(context.ord, new BitDocIdSet(result));

        final Fields fields = reader.fields();
        Terms terms = fields.terms(RangeIndexWorker.FIELD_ID);
        if (terms != null) {
            TermsEnum termsEnum = terms.iterator(null);
            DocsEnum docs = null;
            for (NodeSetIterator i = contextSet.iterator(); i.hasNext(); ) {
                NodeProxy node = i.next();
                BytesRef lowerBound = new BytesRef(LuceneUtil.createId(node.getDoc().getDocId(), node.getNodeId()));
                BytesRef upperBound = new BytesRef(LuceneUtil.createId(node.getDoc().getDocId(), node.getNodeId().nextSibling()));

                if (termsEnum.seekCeil(lowerBound) != TermsEnum.SeekStatus.END) {
                    do {
                        BytesRef nextId = termsEnum.term();
                        if (nextId.compareTo(upperBound) < 0) {
                            docs = termsEnum.docs(null, docs, DocsEnum.FLAG_NONE);
                            while (docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                                int id = docs.docID();
                                result.set(id);
                            }
                        } else {
                            break;
                        }
                    } while (termsEnum.next() != null);
                }
            }
        }
    }

    private static boolean startsWith(byte[] key, int offset, int length, byte[] prefix) {
        if (length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (key[offset + i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }
}
