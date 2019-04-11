/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.machine.processor.rxjava;

import io.reactivex.functions.Function;
import org.apache.tinkerpop.machine.function.branch.RepeatBranch;
import org.apache.tinkerpop.machine.traverser.Traverser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class RepeatStart<C, S> implements Function<Traverser<C, S>, List<List>> {

    private final ThreadLocal<RepeatBranch<C, S>> repeatBranch;

    RepeatStart(final RepeatBranch<C, S> repeatBranch) {
        this.repeatBranch = ThreadLocal.withInitial(repeatBranch::clone);
    }

    @Override
    public List<List> apply(final Traverser<C, S> traverser) {
        final List<List> list = new ArrayList<>();
        if (1 == this.getRepeatBranch().getUntilLocation()) {
            if (this.getRepeatBranch().getUntil().filterTraverser(traverser)) {
                list.add(List.of(0, traverser.repeatDone(this.getRepeatBranch())));
            } else if (2 == this.getRepeatBranch().getEmitLocation() && this.getRepeatBranch().getEmit().filterTraverser(traverser)) {
                list.add(List.of(1, traverser));
                list.add(List.of(0, traverser.repeatDone(this.getRepeatBranch())));
            } else
                list.add(List.of(1, traverser));
        } else if (1 == this.getRepeatBranch().getEmitLocation()) {
            if (this.getRepeatBranch().getEmit().filterTraverser(traverser))
                list.add(List.of(0, traverser.repeatDone(this.getRepeatBranch())));
            if (2 == this.getRepeatBranch().getUntilLocation() && this.getRepeatBranch().getUntil().filterTraverser(traverser)) {
                list.add(List.of(0, traverser.repeatDone(this.getRepeatBranch())));
            } else
                list.add(List.of(1, traverser));
        }
        return list;
    }

    private RepeatBranch<C, S> getRepeatBranch() {
        return this.repeatBranch.get();
    }
}
