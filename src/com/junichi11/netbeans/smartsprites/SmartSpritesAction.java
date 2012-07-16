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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.carrot2.labs.smartsprites.SmartSpritesParameters;
import org.carrot2.labs.smartsprites.SpriteBuilder;
import org.carrot2.labs.smartsprites.message.MemoryMessageSink;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class SmartSpritesAction extends NodeAction {
    private static final long serialVersionUID = 6452478335570946050L;
    public SmartSpritesAction() {
    }

    @Override
    protected void performAction(Node[] nodes) {
        if(nodes.length < 1){
            // nothing
            return;
        }
        Node currentNode = nodes[0];
        FileObject currentFileObject = null;
        DataObject dataObject = currentNode.getCookie(DataObject.class);
        if(dataObject != null){
            currentFileObject = dataObject.getPrimaryFile();
        }
        if(currentFileObject == null){
            // nothing
            return;
        }
        
        SmartSpritesParameters ssp = new SmartSpritesParameters(currentFileObject.getPath());
        MemoryMessageSink messageSink = new MemoryMessageSink();
        MessageLog log = new MessageLog(messageSink);
        try {
            new SpriteBuilder(ssp, log).buildSprites();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        // refresh
        FileUtil.refreshFor(FileUtil.toFile(currentFileObject));
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(SmartSpritesAction.class, "LBL_SmartSpritesActionName");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
