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

package com.jeantessier.dependencyfinder.webwork;

import com.jeantessier.classreader.LoadEvent;
import com.jeantessier.dependencyfinder.VerboseListenerBase;

import java.io.PrintWriter;

public class VerboseListener extends VerboseListenerBase {
    private PrintWriter out;

    private int classCount = 0;

    public VerboseListener(PrintWriter out) {
        this.out = out;
    }

    public int getClassCount() {
        return classCount;
    }

    public void print(String s) {
        out.println(s);
    }

    public void beginGroup(LoadEvent event) {
        super.beginGroup(event);

        out.println();
        out.print("\tSearching ");
        out.print(getCurrentGroup().getName());
        if (getCurrentGroup().getSize() >= 0) {
            out.print(" (");
            out.print(getCurrentGroup().getSize());
            out.print(" files)");
        }
        out.print(" ...");
        out.println();
    }

    public void beginFile(LoadEvent event) {
        super.beginFile(event);

        out.print(getRatioIndicator());
    }

    public void endClassfile(LoadEvent event) {
        super.endClassfile(event);

        classCount++;

        out.print("\t\tGetting dependencies from ");
        out.print(event.getClassfile());
        out.print(" ...");
        out.println();
    }

    public void endFile(LoadEvent event) {
        super.endFile(event);

        if (!getVisitedFiles().contains(event.getFilename())) {
            out.print("\t\t<i>Skipping ");
            out.print(event.getFilename());
            out.print(" ...</i>");
            out.println();
        }
    }
}