/*
 *  Copyright (c) 2001-2007, Jean Tessier
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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;

import org.apache.log4j.*;

import com.jeantessier.dependency.*;
import com.jeantessier.commandline.*;

public class DependencyCycles extends Command {
    public DependencyCycles() throws CommandLineException {
        super("DependencyCycles");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("Default is text output to the console.");
        out.println();
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(XMLPrinter.DEFAULT_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        populateCommandLineSwitchesForScoping();

        getCommandLine().addSingleValueSwitch("maximum-cycle-length");

        getCommandLine().addToggleSwitch("xml");
        getCommandLine().addToggleSwitch("validate");
    }

    protected boolean validateCommandLine(PrintStream out) {
        boolean result = super.validateCommandLine(out);

        result &= validateCommandLineForScoping(out);

        return result;
    }

    protected void doProcessing() throws Exception {
        NodeFactory factory = new NodeFactory();

        for (String filename : getCommandLine().getParameters()) {
            Logger.getLogger(DependencyMetrics.class).info("Reading " + filename);
            getVerboseListener().print("Reading " + filename);

            if (filename.endsWith(".xml")) {
                NodeLoader loader = new NodeLoader(factory, getCommandLine().getToggleSwitch("validate"));
                loader.addDependencyListener(getVerboseListener());
                loader.load(filename);
            }

            Logger.getLogger(DependencyMetrics.class).info("Read \"" + filename + "\".");
        }

        CycleDetector detector = new CycleDetector(getScopeCriteria());

        if (getCommandLine().isPresent("maximum-cycle-length")) {
            detector.setMaximumCycleLength(Integer.parseInt(getCommandLine().getSingleSwitch("maximum-cycle-length")));
        }

        detector.traverseNodes(factory.getPackages().values());

        getVerboseListener().print("Printing the graph ...");

        CyclePrinter printer;
        if (getCommandLine().isPresent("xml")) {
            printer = new XMLCyclePrinter(out, getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        } else {
            printer = new TextCyclePrinter(out);
        }

        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        printer.visitCycles(detector.getCycles());
    }

    public static void main(String[] args) throws Exception {
        new DependencyCycles().run(args);
    }
}