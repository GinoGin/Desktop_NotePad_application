import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.KeyEvent;
import java.io.*;

public class FileOperation {

    NotePad npd;
    String filename;
    String applicationTitle="NotePad - JavaTpoint";

    boolean saved;
    boolean newFileFlag;

    File file=null;
    JFileChooser chooser;


    public FileOperation(NotePad npd){
        this.npd=npd;

        saved=true;
        newFileFlag=true;
        filename= new String("Untitled");
        npd.f.setTitle(filename+"-"+applicationTitle);
        chooser=new JFileChooser();
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Java Source Files(*.java)",".java"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("ext Files(*.txt)",".txt"));
        chooser.setCurrentDirectory(new File("."));
    }

    void newFile(){
        if(!confirmSave()) return;
        this.npd.ta.setText("");
        filename="untitled";
        file=new File(filename);
        saved=true;
        newFileFlag=true;
        this.npd.f.setTitle(filename+"-"+applicationTitle);


    }

    boolean saveFile(File temp){
        FileWriter fout=null;
        try{
            fout=new FileWriter(temp);
            fout.write(npd.ta.getText());
        }catch (Exception e){updateStatus(temp,false);return false;}
        finally {
            try{
                fout.close();
            }catch (Exception e){}}
            updateStatus(temp,true);
            return true;

    }
    boolean saveAsFile(){
        File temp=null;
        chooser.setDialogTitle("Save As..");
        chooser.setApproveButtonText("Save Now");
        chooser.setApproveButtonMnemonic(KeyEvent.VK_S);
        chooser.setApproveButtonToolTipText("Click me to save");

        do{
            if(chooser.showSaveDialog(this.npd.f)!=JFileChooser.APPROVE_OPTION)
                return false;
            temp=chooser.getSelectedFile();
            if(!temp.exists())
                break;
            if(JOptionPane.showConfirmDialog(
                    this.npd.f,"<html>"+temp.getPath()+" already exist.<br>" +
                            "Do you want to replace it?<html>","Save As",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                break;
        }while(true);

        return saveFile(temp);
    }

    boolean saveThisFile(){
        if(!newFileFlag)
        {return saveFile(file);}
        return saveAsFile();
    }

    void updateStatus(File temp,boolean saved){
        if(saved){
            this.saved=true;
            filename=new String(temp.getName());
//            if(!temp.canWrite()){
//                filename+="(read Only)";
//                newFileFlag=true;
//            }
            file=temp;
            this.npd.f.setTitle(filename+"-"+applicationTitle);
            npd.statusBar.setText("File "+temp.getPath()+" saved/opened successfully");
            newFileFlag=false;
        }else{
            npd.statusBar.setText("Failed to save/open "+temp.getPath());
        }
    }
    boolean openFile(File temp){
        FileInputStream fis=null;
        BufferedReader br=null;
        try{
            fis=new FileInputStream(temp);
            br =new BufferedReader(new InputStreamReader(fis));
            String str="";
            while(str!=null){
                str=br.readLine();
                if (str==null) break;
                this.npd.ta.append(str+"\n");
            }
        }catch (Exception e){
                updateStatus(temp,false);
                return false;
        }
        finally {
            try{
                fis.close();
                br.close();
            }catch (Exception io){}
        }
        updateStatus(temp,true);
        this.npd.ta.setCaretPosition(0);
        return true;

    }

    void openFile(){
        if(!confirmSave()){
            return;
        }
        chooser.setDialogTitle("Open file");
        chooser.setApproveButtonText("Open");
        chooser.setApproveButtonMnemonic(KeyEvent.VK_O);
        chooser.setApproveButtonToolTipText("Click me to open");
        File temp=null;
        do{
            if (chooser.showOpenDialog(this.npd.f) != JFileChooser.APPROVE_OPTION) return;
            temp=chooser.getSelectedFile();
            if(temp.exists()) break;
            JOptionPane.showConfirmDialog(npd.f,"<html>"+temp.getName()+" file not found<br>"
            +"Please verify the given file name","Open",JOptionPane.INFORMATION_MESSAGE);
        }while(true);
        this.npd.ta.setText("");
        if(!openFile(temp)) {
            saved=true;
            filename="Untitled";
            this.npd.f.setTitle(filename+"-"+applicationTitle);
        }

    }

    boolean confirmSave(){
        String str="<html>The text in the file "+filename+" has been changed<br>"
                +"do you want to save?";
        if(!this.saved){
            int x=JOptionPane.showConfirmDialog(this.npd.f,str,applicationTitle,JOptionPane.YES_NO_OPTION);
            if(x==JOptionPane.CANCEL_OPTION) return false;
            if (x==JOptionPane.YES_OPTION && !saveAsFile()) return false;
        }
        return true;
    }


}
