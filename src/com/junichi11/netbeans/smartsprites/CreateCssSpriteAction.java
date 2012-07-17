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

import java.awt.Image;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
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

/**
 *
 * @author junichi11
 */
public class CreateCssSpriteAction extends NodeAction{
    private static final String CSS_EXT = "css"; // NOI18N
    private static final String IMAGES_REG_PATTERN = "png|jpg|jpeg|gif"; // NOI18N
    private static final String PX = "px"; // NOI18N
    private static final long serialVersionUID = 5388357699329074995L;
    private String attribute = "."; // NOI18N
    private String cssName = "style"; // NOI18N
    private String cssspriteDirName = "csssprite"; // NOI18N

    public CreateCssSpriteAction() {
    }

    @Override
    protected void performAction(Node[] nodes) {
        if(nodes.length < 1){
            return;
        }
        Node currentNode = nodes[0];
        DataObject currentDataObject = currentNode.getCookie(DataObject.class);
        FileObject imageDirectory = currentDataObject.getPrimaryFile();
        FileObject[] children = imageDirectory.getChildren();
        // image only
        children = filter(children);
        
        // generage a css file
        try {
            constructCss(children);
            SmartSpritesParameters ssp = new SmartSpritesParameters(imageDirectory.getFileObject(cssspriteDirName).getPath());
            MemoryMessageSink messageSink = new MemoryMessageSink();
            MessageLog log = new MessageLog(messageSink);
            new SpriteBuilder(ssp, log).buildSprites();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        // delte background-image
        FileObject spriteFile = imageDirectory.getFileObject(cssspriteDirName + "/"+ cssName +"-sprite.css"); // NOI18N
        if(spriteFile != null){
            try {
                List<String> asLines = spriteFile.asLines();
                PrintWriter pw = new PrintWriter(spriteFile.getOutputStream());
                for (String line : asLines) {
                    if (!line.contains("background-image")) { // NOI18N
                        pw.println(line);
                    }
                }
                pw.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        // refresh
        FileUtil.refreshFor(FileUtil.toFile(imageDirectory));
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CreateCssSpriteAction.class, "LBL_CreateCssSpriteActionName"); // NOI18N
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    /**
     * Create a css file for smartsprites
     * @param files image files
     * @throws IOException 
     */
    private void constructCss(FileObject[] files) throws IOException{
        if(files.length < 1){
            return ;
        }
        FileObject parent = files[0].getParent();
        FileObject cssspriteDirectory = parent.getFileObject(cssspriteDirName);
        if(cssspriteDirectory == null){
            cssspriteDirectory = parent.createFolder(cssspriteDirName);
        }
        FileObject cssFile = cssspriteDirectory.getFileObject(cssName, CSS_EXT);
        if(cssFile == null){
            cssFile = cssspriteDirectory.createData(cssName, CSS_EXT);
        }
        SmartSpritesCssBuilder cssBuilder = new SmartSpritesCssBuilder(cssFile);
        
        cssBuilder.open();
        
        for(FileObject file : files){
            Image image = ImageIO.read(file.getInputStream());
            cssBuilder.makeSelector(attribute + file.getName());
            cssBuilder.addProperty("width", image.getWidth(null) + PX); // NOI18N
            cssBuilder.addProperty("height", image.getHeight(null) + PX); // NOI18N
            cssBuilder.addProperty("background-image", "url('../" + file.getNameExt() + "')"); // NOI18N
            cssBuilder.closeSelector();
        }
        cssBuilder.close();
    }
    
    /**
     * Image files filter
     * @param files files
     * @return FileObject[]
     */
    private FileObject[] filter(FileObject[] files){
        ArrayList<FileObject> list = new ArrayList<FileObject>();
        for(FileObject file : files){
            String ext = file.getExt();
            ext = ext.toLowerCase();
            if(ext.matches(IMAGES_REG_PATTERN)){
                list.add(file);
            }
        }
        return list.toArray(new FileObject[0]);
    }
}
