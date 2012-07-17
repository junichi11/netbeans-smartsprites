/*
 * The MIT License
 *
 * Copyright 2012 junichi11.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.junichi11.netbeans.smartsprites;

import java.io.IOException;
import java.io.PrintWriter;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public class SmartSpritesCssBuilder implements CssBuilder{
    private static final String INDENT_SPACE = "  "; // NOI18N
    private static final String PROPERTY_PATTERN = "%s: %s;"; // NOI18N
    private static final String SEMICOLON = ";"; // NOI18N
    private FileObject css = null;
    private PrintWriter printWriter = null;
    private boolean isInSelector = false;
    private String spriteRef = "csssprite"; // NOI18N
    private String spriteImageName = "sprite"; // NOI18N
    private String spriteLayout = "vertical"; // NOI18N
    private String selectorPrefix = ""; // NOI18N
    private String selectorSuffix = "-sprite"; // NOI18N
    
    public SmartSpritesCssBuilder(FileObject css){
        this.css = css;
    }
    
    @Override
    public void open() {
        try {
            printWriter = new PrintWriter(css.getOutputStream());
            makeDirective();
        } catch (FileAlreadyLockedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void close() {
        printWriter.close();
    }

    @Override
    public void makeSelector(String selector) {
        if(!isInSelector){
            printWriter.println(selectorPrefix + selector + selectorSuffix + " {"); // NOI18N
            isInSelector = true;
        }
    }

    @Override
    public void closeSelector() {
        if(printWriter != null){
            isInSelector = false;
            printWriter.println("}"); // NOI18N
            printWriter.println();
        }
    }

    @Override
    public void addProperty(String key, String value) {
        if(isInSelector){
            printWriter.print(INDENT_SPACE);
            printWriter.format(PROPERTY_PATTERN, key, value);
            if(key.equals("background-image")){ // NOI18N
                printWriter.format("/** sprite-ref: %s */", spriteRef); // NOI18N
            }
            printWriter.println();
        }
    }

    @Override
    public void makeString(String string) {
        if(isInSelector){
            printWriter.print(INDENT_SPACE);
        }
        printWriter.println(string);
    }
    
    private void makeDirective(){
        StringBuilder sb = new StringBuilder();
        sb.append("/** "); // NOI18N
        sb.append("sprite: "); // NOI18N
        sb.append(spriteRef);
        sb.append(SEMICOLON);
        sb.append("sprite-image: "); // NOI18N
        sb.append("url('"); // NOI18N
        sb.append(spriteImageName);
        sb.append(".png')"); // NOI18N
        sb.append(SEMICOLON);
        sb.append("sprite-layout: "); // NOI18N
        sb.append(spriteLayout);
        sb.append(SEMICOLON);
        sb.append("*/"); // NOI18N
        printWriter.println(sb.toString());
    }
}
