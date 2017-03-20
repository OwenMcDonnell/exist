xquery version "3.0";

(:~
 : Tests for Computed Namespace Constructors
 :)
module namespace cnc = "http://exist-db.org/xquery/test/computed-namespace-constructors";

import module namespace test = "http://exist-db.org/xquery/xqsuite" at "resource:org/exist/xquery/lib/xqsuite/xqsuite.xql";

declare namespace ok="http://place-on-interwebz.com/a-ok";
declare namespace doh="http://also-on-interwebz.com/problem";


declare
    %test:assertError("XQDY0102")
function cnc:cannot-override-no-ns() {
    element root {namespace {""} {"http://also-on-interwebz.com/problem"},
        namespace ok {"http://place-on-interwebz.com/a-ok"},
        for $n in 1 to 3
        return
            element stuff {$n}
    }
};

declare
    %test:assertEquals(3)
function cnc:ns-default-constructor() {
    count(
        element ok:root {namespace {""} {"http://also-on-interwebz.com/problem"},
            namespace ok {"http://place-on-interwebz.com/a-ok"},
            for $n in 1 to 3
            return
                element stuff {$n}
        }/stuff
    )
};
