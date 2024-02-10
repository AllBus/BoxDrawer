/*
 * JDXF Library
 * 
 *   Copyright (C) 2018, Jonathan Sevy <jsevy@jsevy.com>
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 * 
 */

package com.jsevy.jdxf;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Just a special case of DXFObjects used for graphical entities.
 * 
 * @author jsevy
 *
 */
public class DXFEntity extends DXFDatabaseObject
{
    private static int digits = 10;
    
    
    // Layer entity belongs to; null if no layer specified
    protected DXFLayer layer = null;
    
    
    /**
     * Set the number of digits when a quantity is truncated with a call to setPrecision()
     * 
     * @param decimalDigits  Number of digits following the decimal point, between 0 and 16
     */
    public static void setPrecisionDigits(int decimalDigits)
    {
        digits = decimalDigits;
        
        // put in the range of 0 to 16
        if (digits < 0)
            digits = 0;
        if (digits > 16)
            digits = 16;
        
    }
    
    
    /**
     * Utility method used by subclasses to truncate output of decimal quantities
     * to the number of digits specified through a call to setPrecisionDigits; default is 10.
     * 
     * @param value  Double value to be truncated
     * @return  Input with digits following the decimal point truncated to the number of digits specified through a call to setPrecisionDigits
     */
    protected static double setPrecision(double value)
    {
        return (new BigDecimal(value)).setScale(digits, RoundingMode.HALF_UP).doubleValue();
    }
    
    
    //public DXFEntity()
    //{
    //    super();
    //}
    
    
    public DXFEntity(DXFLayer layer)
    {
        this.layer = layer;
    }
    
    
    public String toDXFString()
    {
        // print out handle and superclass marker(s)
        StringBuilder result = new StringBuilder(super.toDXFString());
        
        // print out base subclass marker for entities
        result.append("100\nAcDbEntity\n");
        
        // print out layer name; layer == null indicates the default layer "0"
        String layerName;
        if (layer != null)
        {
            layerName = layer.getName();
        }
        else
        {
            layerName = "0";
        }
        result.append("8\n" + layerName + "\n");
        
        return result.toString();
    }
    
    /* Should be overridden by classes that can be used as Hatch boundaries */
    public String getDXFHatchInfo()
    {
        return "";
    }
    
    
    
    
}