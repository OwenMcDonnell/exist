/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2011-2012 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package org.exist.collections.triggers;

import java.util.List;

import org.apache.log4j.Logger;
import org.exist.collections.Collection;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;

/**
 *
 * @author aretter
 */
public class CollectionTriggersVisitor extends AbstractTriggersVisitor<CollectionTrigger> implements CollectionTrigger {

    protected final static Logger LOG = Logger.getLogger(CollectionTriggersVisitor.class);
    
    public CollectionTriggersVisitor(CollectionTriggerProxies proxies) {
        super(proxies);
    }

    public CollectionTriggersVisitor(List<CollectionTrigger> triggers) {
        super(triggers);
    }
    
    private void log(Exception e) {
    	LOG.error(e.getMessage(), e);
    }

    @Override
    public void beforeCreateCollection(DBBroker broker, Txn txn, XmldbURI uri) throws TriggerException {
        for(final CollectionTrigger trigger : getTriggers()) {
            trigger.beforeCreateCollection(broker, txn, uri);
        }
    }

    @Override
    public void afterCreateCollection(DBBroker broker, Txn txn, Collection collection) {
        for(final CollectionTrigger trigger : getTriggers()) {
        	try {
        		trigger.afterCreateCollection(broker, txn, collection);
			} catch (TriggerException e) { log(e); }
        }
    }

    @Override
    public void beforeCopyCollection(DBBroker broker, Txn txn, Collection collection, XmldbURI newUri) throws TriggerException {
        for(final CollectionTrigger trigger : getTriggers()) {
            trigger.beforeCopyCollection(broker, txn, collection, newUri);
        }
    }

    @Override
    public void afterCopyCollection(DBBroker broker, Txn txn, Collection collection, XmldbURI oldUri) {
        for(final CollectionTrigger trigger : getTriggers()) {
        	try {
        		trigger.afterCopyCollection(broker, txn, collection, oldUri);
			} catch (TriggerException e) { log(e); }
        }
    }

    @Override
    public void beforeMoveCollection(DBBroker broker, Txn txn, Collection collection, XmldbURI newUri) throws TriggerException {
        for(final CollectionTrigger trigger : getTriggers()) {
            trigger.beforeMoveCollection(broker, txn, collection, newUri);
        }
    }

    @Override
    public void afterMoveCollection(DBBroker broker, Txn txn, Collection collection, XmldbURI oldUri) {
        for(final CollectionTrigger trigger : getTriggers()) {
        	try {
        		trigger.afterMoveCollection(broker, txn, collection, oldUri);
			} catch (TriggerException e) { log(e); }
        }
    }

    @Override
    public void beforeDeleteCollection(DBBroker broker, Txn txn, Collection collection) throws TriggerException {
        for(final CollectionTrigger trigger : getTriggers()) {
            trigger.beforeDeleteCollection(broker, txn, collection);
        }
    }

    @Override
    public void afterDeleteCollection(DBBroker broker, Txn txn, XmldbURI uri) {
        for(final CollectionTrigger trigger : getTriggers()) {
            try {
				trigger.afterDeleteCollection(broker, txn, uri);
			} catch (TriggerException e) { log(e); }
        }
    }
}