import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class NotePad implements ActionListener {
    JFrame f;
    JTextArea ta;
    JLabel statusBar;

    String filename="Untitled";
    String applicationTitle="JavaPad";
    FileOperation fileHandler;

    JMenuItem undoItem,cutItem,copyItem,pasteItem,deleteItem,findItem,findNextItem,replaceItem,goToItem,selectAllItem;


    /** CONSTRUCTOR  **********************/
    public NotePad(){
        f=new JFrame(filename+"-"+applicationTitle);
        ta=new JTextArea(30,60);
        statusBar=new JLabel("||  Ln 0, Col 0 ||",JLabel.RIGHT);

        f.add(new JScrollPane(ta),BorderLayout.CENTER);
        f.add(statusBar, BorderLayout.SOUTH);
        f.add(new JLabel(""),BorderLayout.NORTH);
        f.add(new JLabel(" "),BorderLayout.WEST);
        f.add(new JLabel(" "),BorderLayout.EAST);
        createMenuBar(f);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        fileHandler = new FileOperation(this);

        ta.addCaretListener(
                new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        int lineNumber=0,column=0,pos=0;
                        try{
                            pos=ta.getCaretPosition();
                            lineNumber=ta.getLineOfOffset(pos);
                            column=pos-ta.getLineStartOffset(lineNumber);
                        }catch (Exception eee){}
                        if(ta.getText().length()==0){lineNumber=0;column=0;pos=0;}
                        statusBar.setText("||  Ln "+(lineNumber+1)+", Col "+(column+1));
                    }
                }
        );
        DocumentListener myListener = new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e){fileHandler.saved=false;}
            public void removeUpdate(DocumentEvent e){fileHandler.saved=false;}
            public void insertUpdate(DocumentEvent e){fileHandler.saved=false;}
        };
        ta.getDocument().addDocumentListener(myListener);

    }
    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd= e.getActionCommand();
        if(cmd.equals("New")){
            fileHandler.newFile();
        }
        else if(cmd.equals("Open")){
            fileHandler.openFile();
        }
        else if(cmd.equals("Save")){
            fileHandler.saveThisFile();
        }
        else if(cmd.equals("SaveAs")){
            fileHandler.saveAsFile();
        }
        else if(cmd.equals("Cut"))
            this.ta.cut();
        else  if(cmd.equals("Copy"))
            this.ta.copy();
        else if(cmd.equals("Paste"))
            this.ta.paste();
        else if(cmd.equals("Delete"))
            this.ta.replaceSelection("");


    }
    /************************************************/



    /*************************************************/
    JMenuItem createMenuItem(String s,int key,JMenu menu,ActionListener al){
        JMenuItem temp= new JMenuItem(s,key);
        temp.addActionListener(al);
        menu.add(temp);
        return temp;
    }

    JMenuItem createMenuItem(String s, int key, JMenu menu, int aclKey, ActionListener al){
            JMenuItem temp = new JMenuItem(s,key);
            temp.addActionListener(al);
            temp.setAccelerator(KeyStroke.getKeyStroke(aclKey,ActionEvent.CTRL_MASK));
            menu.add(temp);
            return temp;
    }

    /***************************************************/
    JMenu createMenu(String s,int key,JMenuBar mb){
        JMenu temp=new JMenu(s);
        temp.setMnemonic(key);
        mb.add(temp);
        return temp;
    }

    /***************************************************/
    void createMenuBar(JFrame f){

        JMenuBar mb =new JMenuBar();
        JMenuItem temp;
        /** creating menu **/
        JMenu fileMenu=createMenu("File", KeyEvent.VK_F,mb);
        JMenu editMenu =createMenu("Edit",KeyEvent.VK_E,mb);
        JMenu formatMenu = createMenu("Format",KeyEvent.VK_O,mb);
        JMenu viewMenu = createMenu("View",KeyEvent.VK_V,mb);
        JMenu helpMenu = createMenu("Help",KeyEvent.VK_H,mb);

        /** creating MenuItem **/
        //file menu
        createMenuItem("New",KeyEvent.VK_N,fileMenu,KeyEvent.VK_N,this);
        createMenuItem("Open",KeyEvent.VK_O,fileMenu,KeyEvent.VK_O,this);
        createMenuItem("Save",KeyEvent.VK_S,fileMenu,KeyEvent.VK_S,this);
        createMenuItem("SaveAs",KeyEvent.VK_A,fileMenu,this);
        fileMenu.addSeparator();
        temp=createMenuItem("PageSetup",KeyEvent.VK_U,fileMenu,this);
        temp.setEnabled(false);
        createMenuItem("Print",KeyEvent.VK_P,fileMenu,this);
        fileMenu.addSeparator();
        createMenuItem("Exit",KeyEvent.VK_X,fileMenu,this);

        //edit menu
        undoItem=createMenuItem("Undo",KeyEvent.VK_U,editMenu,KeyEvent.VK_Z,this);
        cutItem=createMenuItem("Cut",KeyEvent.VK_X,editMenu,KeyEvent.VK_X,this);
        copyItem=createMenuItem("Copy",KeyEvent.VK_C,editMenu,KeyEvent.VK_C,this);
        pasteItem=createMenuItem("Paste",KeyEvent.VK_V,editMenu,KeyEvent.VK_V,this);
        deleteItem=createMenuItem("Delete",KeyEvent.VK_L,editMenu,this);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        editMenu.addSeparator();
        findItem=createMenuItem("Find",KeyEvent.VK_F,editMenu,KeyEvent.VK_F,this);
        findNextItem=createMenuItem("FindNext",KeyEvent.VK_N,editMenu,this);
        findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
        replaceItem=createMenuItem("Replace",KeyEvent.VK_R,editMenu,KeyEvent.VK_H,this);
        goToItem=createMenuItem("Goto",KeyEvent.VK_G,editMenu,KeyEvent.VK_G,this);
        selectAllItem=createMenuItem("SelectAll",KeyEvent.VK_A,editMenu,KeyEvent.VK_A,this);

        MenuListener editMenuListener = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if(NotePad.this.ta.getText().length()==0){
                    findItem.setEnabled(false);
                    findNextItem.setEnabled(false);
                    replaceItem.setEnabled(false);
                    selectAllItem.setEnabled(false);
                    goToItem.setEnabled(false);
                }else{
                    findItem.setEnabled(true);
                    findNextItem.setEnabled(true);
                    replaceItem.setEnabled(true);
                    selectAllItem.setEnabled(true);
                    goToItem.setEnabled(true);
                }
                if(NotePad.this.ta.getSelectionStart()==ta.getSelectionEnd()){
                    cutItem.setEnabled(false);
                    copyItem.setEnabled(false);
                    deleteItem.setEnabled(false);
                }else{
                    cutItem.setEnabled(true);
                    copyItem.setEnabled(true);
                    deleteItem.setEnabled(true);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        };

        editMenu.addMenuListener(editMenuListener);
        f.setJMenuBar(mb);

    }



    public static  void main(String[] args){
        new NotePad();
    }


}
