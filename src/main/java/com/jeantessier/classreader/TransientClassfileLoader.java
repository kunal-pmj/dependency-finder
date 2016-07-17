/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.classreader;

import com.jeantessier.classreader.impl.DefaultClassfileFactory;

import java.io.DataInput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class TransientClassfileLoader extends ClassfileLoaderEventSource {
    public TransientClassfileLoader() {
        this(new DefaultClassfileFactory());
    }

    private TransientClassfileLoader(ClassfileFactory factory) {
        super(factory);
    }

    public TransientClassfileLoader(ClassfileLoaderDispatcher dispatcher) {
        this(new DefaultClassfileFactory(), dispatcher);
    }

    private TransientClassfileLoader(ClassfileFactory factory, ClassfileLoaderDispatcher dispatcher) {
        super(factory, dispatcher);
    }

    public Classfile getClassfile(String name) {
        return null;
    }

    public Collection<Classfile> getAllClassfiles() {
        return Collections.emptyList();
    }

    public Collection<String> getAllClassNames() {
        return Collections.emptyList();
    }

    protected Classfile load(DataInput in) throws IOException {
        return getFactory().create(this, in);
    }
}