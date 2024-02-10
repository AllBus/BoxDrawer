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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;



/**
 * Class representing Xrecord for use in OBJECTS table. Xrecords contain arbitrary strings not tied to
 * geometric objects for things like document title, author, etc. Each string is assigned to a user-selected
 * group code from 1 - 369 (except 5 and 105).
 * 
 * @author jsevy
 *
 */
public class DXFXrecord extends DXFDatabaseObject
{
    
    protected TreeMap<Integer, String> attributeMap;
    
    
    /**
     * Create an Xrecord object with no associated attributes.
     */
    public DXFXrecord()
    {
        super();
        this.attributeMap = new TreeMap<Integer, String>();
    }
    
    
    /**
     * Get the String associated with the supplied group code, or null if no string is
     * associated.
     *  
     * @param groupCode     Group code value for desired attribute.
     * @return              String associated with supplied group code, or null if none associated
     */
    public String getAttribute(int groupCode)
    {
        return this.attributeMap.get(groupCode);
    }
    
    
    /**
     * Associate the String with the supplied group code in this Xrecord, replacing any that may already be there.
     * From the DXF standard, any value from 1 - 369 may be used, except 5 and 105; note that no error is issued if
     * one of these restricted values is supplied, but the resulting DXF file will not be compliant with the standard
     * and may cause issues when opened with a standard-compliant viewer.
     * 
     * @param groupCode         Group code value to be associated with supplied string, from 1 to 369 excepting 5 and 105
     * @param attributeValue    String to associate with supplied group code
     */
    public void setAttribute(int groupCode, String attributeValue)
    {
        this.attributeMap.put(groupCode, attributeValue);
    }
    
    
    /**
     * Implementation of DXFObject interface method; creates DXF text representing the object.
     */
    public String toDXFString()
    {
        StringBuilder result = new StringBuilder("0\nXRECORD\n");
        
        // print out handle and superclass marker(s)
        result.append(super.toDXFString());
        
        // print out subclass marker
        result.append("100\nAcDbXrecord\n");
        
        // duplicate record cloning flag - keep existing
        result.append("280\n1\n");
        
        // print out list of numbers and values of attributes
        Set<Map.Entry<Integer,String>> entrySet = attributeMap.entrySet();
        Iterator<Map.Entry<Integer,String>> iterator = entrySet.iterator();
        
        while (iterator.hasNext())
        {
            Map.Entry<Integer,String> attribute = iterator.next();
            Integer attributeNumber = attribute.getKey();
            String attributeValue = attribute.getValue();
            result.append(attributeNumber + "\n");
            result.append(attributeValue + "\n");
        }
        
        return result.toString();
    }
    
}