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

package com.jeantessier.metrics;

import junit.framework.*;

public class TestCounterMeasurement extends TestCase {
	public TestCounterMeasurement(String name) {
		super(name);
	}

	public void testAdd() {
		CounterMeasurement measure = new CounterMeasurement("foobar");

		measure.Add(new Integer(1));

		assertEquals(1, measure.Value().intValue());
		assertEquals(1.0, measure.Value().doubleValue(), 0.01);

		measure.Add(new Float(0.5));

		assertEquals(1, measure.Value().intValue());
		assertEquals(1.5, measure.Value().doubleValue(), 0.01);
	}

	public void testSubstract() {
		CounterMeasurement measure = new CounterMeasurement("foobar");

		measure.Add(new Integer(-1));

		assertEquals(-1, measure.Value().intValue());
		assertEquals(-1.0, measure.Value().doubleValue(), 0.01);

		measure.Add(new Float(0.4));

		assertEquals(0, measure.Value().intValue());
		assertEquals(-0.6, measure.Value().doubleValue(), 0.01);

		measure.Add(new Float(0.1));

		assertEquals(0, measure.Value().intValue());
		assertEquals(-0.5, measure.Value().doubleValue(), 0.01);
	}
}
