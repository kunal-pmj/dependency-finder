/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import java.io.*;

import org.apache.log4j.*;

public class LocalVariable implements Visitable {
    private LocalVariableTable_attribute local_variable_table;
    private int                          start_pc;
    private int                          length;
    private int                          name_index;
    private int                          descriptor_index;
    private int                          index;

    public LocalVariable(LocalVariableTable_attribute local_variable_table, DataInputStream in) throws IOException {
		LocalVariableTable(local_variable_table);

		start_pc = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("start PC: " + start_pc);

		length = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("length: " + length);

		name_index       = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("name: " + name_index + " (" + Name() + ")");

		descriptor_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("descriptor: " + descriptor_index + " (" + Descriptor() + ")");

		index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("index: " + index);
    }

    public LocalVariableTable_attribute LocalVariableTable() {
		return local_variable_table;
    }

    private void LocalVariableTable(LocalVariableTable_attribute local_variable_table) {
		this.local_variable_table = local_variable_table;
    }

    public int StartPC() {
		return start_pc;
    }

    public int Length() {
		return length;
    }

    public int NameIndex() {
		return name_index;
    }

    public UTF8_info RawName() {
		return (UTF8_info) LocalVariableTable().Classfile().ConstantPool().get(name_index);
    }

    public String Name() {
		return RawName().toString();
    }

    public int DescriptorIndex() {
		return descriptor_index;
    }

    public UTF8_info RawDescriptor() {
		return (UTF8_info) LocalVariableTable().Classfile().ConstantPool().get(descriptor_index);
    }

    public String Descriptor() {
		return RawDescriptor().toString();
    }

    public int Index() {
		return index;
    }

    public String toString() {
		return "Local variable " + Descriptor() + " " + Name();
    }

    public void Accept(Visitor visitor) {
		visitor.VisitLocalVariable(this);
    }
}
