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


/**
 * Class representing a DimStyle table; need to extend DXFTable because has some extra stuff
 * @author jsevy
 *
 */
public class DXFDimStyleTable extends DXFTable
{
    private static final long serialVersionUID = 1L;


    /**
     * Create a DimStyle table header block
     * 
     * @param name	name of table record
     */
    public DXFDimStyleTable(String name)
    {
        super(name);
    }
    
    
    /**
     * Implementation of DXFObject interface method; creates DXF text representing the object.
     */
    public String toDXFString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("0\nTABLE\n");
        result.append("2\n" + name + "\n");
        
        // print out handle
        result.append(myDXFDatabaseObject.toDXFString());
        
        // write out subclass marker
        result.append("100\nAcDbSymbolTable\n");
        
        // add number of elements in table
        result.append("70\n" + this.size() + "\n");
        
        // write out subclass marker
        result.append("100\nAcDbDimStyleTable\n");
        
        // add number of elements in table
        result.append("71\n1\n");
        
        // print out all of table records
        for (int i = 0; i < this.size(); i++)
        {
            result.append(this.elementAt(i).toDXFString());
        }
        
        result.append("0\nENDTAB\n");
        
        return result.toString();
    }
    
}