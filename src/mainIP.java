/*
    Project:    Exercise 04
    Course:     CS 335
    Author:     Jared Rigdon
    Purpose:    User 5x5 kernal to "blur" the selected image in 5 variants

    Reference: Heavily based on the MyImageProc.java file used in class
 */


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;

public class mainIP extends JFrame {


    // Instance variables
    private BufferedImage image;   // the image
    private MyImageObj view;       // a component in which to display an image
    private JLabel infoLabel;      // an informative label for the simple GUI
    private JButton Sigma1, Sigma1_5, Sigma2, Sigma2_5, Sigma3;
    private JButton OriginalButton;// Button to restore original image

    // Constructor for the frame
    public mainIP () {

        super();				// call JFrame constructor

        this.buildMenus();		// helper method to build menus
        this.buildComponents();		// helper method to set up components
        this.buildDisplay();		// Lay out the components on the display
    }

    private void buildMenus () {

        final JFileChooser fc = new JFileChooser(".\\src");
        JMenuBar bar = new JMenuBar();
        this.setJMenuBar (bar);
        JMenu fileMenu = new JMenu ("File");
        JMenuItem fileopen = new JMenuItem ("Open");
        JMenuItem fileexit = new JMenuItem ("Exit");

        fileopen.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        int returnVal = fc.showOpenDialog(mainIP.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                image = ImageIO.read(file);
                            } catch (IOException e1){};

                            view.setImage(image);
                            view.showImage();
                        }
                    }
                }
        );
        fileexit.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        System.exit(0);
                    }
                }
        );

        fileMenu.add(fileopen);
        fileMenu.add(fileexit);
        bar.add(fileMenu);
    }

    private void buildComponents() {

        view = new MyImageObj();
        infoLabel = new JLabel("Original Image");
        OriginalButton = new JButton("Original");
        Sigma1 = new JButton("Sigma 1.0");
        Sigma1_5 = new JButton("Sigma 1.5");
        Sigma2 = new JButton("Sigma 2.0");
        Sigma2_5 = new JButton("Sigma 2.5");
        Sigma3 = new JButton("Sigma 3.0");

        OriginalButton.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        view.showImage();
                        infoLabel.setText("Original");
                    }
                }
        );

        Sigma1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.BlurImage(1);
                infoLabel.setText("Sigma 1.0");
            }
        });

        Sigma1_5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.BlurImage(2);
                infoLabel.setText("Sigma 1.5");
            }
        });

        Sigma2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.BlurImage(3);
                infoLabel.setText("Sigma 2.0");
            }
        });

        Sigma2_5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.BlurImage(4);
                infoLabel.setText("Sigma 2.5");
            }
        });

        Sigma3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.BlurImage(5);
                infoLabel.setText("Sigma 3.0");
            }
        });
    }


    // This helper method adds all components to the content pane of the
    // JFrame object.  Specific layout of components is controlled here

    private void buildDisplay () {

        // Build first JPanel
        JPanel controlPanel = new JPanel();
        GridLayout grid = new GridLayout (1, 5);
        controlPanel.setLayout(grid);
        controlPanel.add(infoLabel);
        controlPanel.add(OriginalButton);
        controlPanel.add(Sigma1);
        controlPanel.add(Sigma1_5);
        controlPanel.add(Sigma2);
        controlPanel.add(Sigma2_5);
        controlPanel.add(Sigma3);


        // Build second JPanel
        JPanel thresholdcontrolPanel = new JPanel();
        BorderLayout layout = new BorderLayout (5, 5);
        thresholdcontrolPanel.setLayout (layout);

        // Add panels and image data component to the JFrame
        Container c = this.getContentPane();
        c.add(view, BorderLayout.EAST);
        c.add(controlPanel, BorderLayout.SOUTH);
        c.add(thresholdcontrolPanel, BorderLayout.WEST);

    }


    public BufferedImage readImage (String file) {

        Image image = Toolkit.getDefaultToolkit().getImage("."+file);
        MediaTracker tracker = new MediaTracker (new Component () {});
        tracker.addImage(image, 0);
        try { tracker.waitForID (0); }
        catch (InterruptedException e) {}
        BufferedImage bim = new BufferedImage
                (image.getWidth(this), image.getHeight(this),
                        BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bim.createGraphics();
        big.drawImage (image, 0, 0, this);
        return bim;
    }


    public static void main(String[] argv) {

        JFrame frame = new mainIP();
        frame.setPreferredSize(new Dimension(1800,1000));
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener (
                new WindowAdapter () {
                    public void windowClosing ( WindowEvent e) {
                        System.exit(0);
                    }
                }
        );
    }

    /*****************************************************************

     This is a helper object, which could reside in its own file, that
     extends JLabel so that it can hold a BufferedImage

     I've added the ability to apply image processing operators to the
     image and display the result

     ***************************************************************************/

    public class MyImageObj extends JLabel {

        // instance variable to hold the buffered image
        private BufferedImage bim=null;
        private BufferedImage filteredbim=null;

        //  tell the paintcomponent method what to draw
        private boolean showfiltered=false;

        // here are a few kernels to try
        private final float[] GAUSS5x5SD1 =                 //sigma 1.0
                {0.003765f, 0.015019f, 0.023792f, 0.015019f, 0.003765f,
                        0.015019f, 0.059912f, 0.094907f, 0.059912f, 0.015019f,
                        0.023792f, 0.094907f, 0.150342f, 0.094907f, 0.023792f,
                        0.015019f, 0.059912f, 0.094907f, 0.059912f, 0.015019f,
                        0.003765f, 0.015019f, 0.023792f, 0.015019f, 0.003765f};
        private final float[] GAUSS5X5SD1_5 =               //sigma1.5
                {0.015026f,	0.028569f,	0.035391f,	0.028569f,	0.015026f,
                        0.028569f,	0.054318f,	0.067288f,	0.054318f,	0.028569f,
                        0.035391f,	0.067288f,	0.083355f,	0.067288f,	0.035391f,
                        0.028569f,	0.054318f,	0.067288f,	0.054318f,	0.028569f,
                        0.015026f,	0.028569f,	0.035391f,	0.028569f,	0.015026f};
        private final float[] GAUSS5x5SD2 =                 //sigma 2.0
                {0.023528f, 0.033969f, 0.038393f, 0.033969f, 0.023528f,
                        0.033969f, 0.049045f, 0.055432f, 0.049045f, 0.033969f,
                        0.038393f, 0.055432f, 0.062651f, 0.055432f, 0.038393f,
                        0.033969f, 0.049045f, 0.055432f, 0.049045f, 0.033969f,
                        0.023528f, 0.033969f, 0.038393f, 0.033969f, 0.023528f};
        private final float[] GAUSS5X5SD2_5 =               //sigma2.5
                {0.028672f,	0.036333f,	0.039317f,	0.036333f,	0.028672f,
                        0.036333f,	0.046042f,	0.049824f,	0.046042f,	0.036333f,
                        0.039317f,	0.049824f,	0.053916f,	0.049824f,	0.039317f,
                        0.036333f,	0.046042f,	0.049824f,	0.046042f,	0.036333f,
                        0.028672f,	0.036333f,	0.039317f,	0.036333f,	0.028672f};
        private final float[] GAUSS5x5SD3 =                 //sigma 3.0
                {0.031827f, 0.037541f, 0.039665f, 0.037541f, 0.031827f,
                        0.037541f, 0.044281f, 0.046787f, 0.044281f, 0.037541f,
                        0.039665f, 0.046787f, 0.049434f, 0.046787f, 0.039665f,
                        0.037541f, 0.044281f, 0.046787f, 0.044281f, 0.037541f,
                        0.031827f, 0.037541f, 0.039665f, 0.037541f, 0.031827f};

        // Default constructor
        public MyImageObj() {
        }

        // This constructor stores a buffered image passed in as a parameter
        public MyImageObj(BufferedImage img) {
            bim = img;
            filteredbim = new BufferedImage
                    (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
            setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));

            this.repaint();
        }

        // This mutator changes the image by resetting what is stored
        // The input parameter img is the new image;  it gets stored as an
        //     instance variable
        public void setImage(BufferedImage img) {
            if (img == null) return;
            bim = img;
            filteredbim = new BufferedImage
                    (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
            setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
            showfiltered=false;
            this.repaint();
        }

        // accessor to get a handle to the bufferedimage object stored here
        public BufferedImage getImage() {
            return bim;
        }



        //  apply the blur operator
        //us the 5 jbuttons to pass in 1 - 5 to use the specified kernal
        public void BlurImage(int choice) {
            if (bim == null) return;
            Kernel kernel;
            switch (choice){
                case 1 :  kernel = new Kernel (5, 5, GAUSS5x5SD1);
                         break;
                case 2 :  kernel = new Kernel (5, 5, GAUSS5X5SD1_5);
                        break;
                case 3 :  kernel = new Kernel (5, 5, GAUSS5x5SD2);
                        break;
                case 4 :  kernel = new Kernel (5, 5, GAUSS5X5SD2_5);
                        break;
                case 5 :  kernel = new Kernel (5, 5, GAUSS5x5SD3);
                        break;
                default:  kernel = new Kernel (5, 5, GAUSS5x5SD1);
                        break;
            }
            //Kernel kernel = new Kernel (5, 5, GAUSS5x5SD1);
            ConvolveOp cop = new ConvolveOp (kernel, ConvolveOp.EDGE_NO_OP, null);

            // make a copy of the buffered image
            BufferedImage newbim = new BufferedImage
                    (bim.getWidth(), bim.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
            Graphics2D big = newbim.createGraphics();
            big.drawImage (bim, 0, 0, null);

            // apply the filter the copied image
            // result goes to a filtered copy
            cop.filter(newbim, filteredbim);
            showfiltered=true;
            this.repaint();
        }


        //  show current image by a scheduled call to paint()
        public void showImage() {
            if (bim == null) return;
            showfiltered=false;
            this.repaint();
        }

        //  get a graphics context and show either filtered image or
        //  regular image
        public void paintComponent(Graphics g) {
            Graphics2D big = (Graphics2D) g;
            if (showfiltered)
                big.drawImage(filteredbim, 0, 0, this);
            else
                big.drawImage(bim, 0, 0, this);
        }
    }


}
