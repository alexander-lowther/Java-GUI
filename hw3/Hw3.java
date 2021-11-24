
/*
 * Alex Lowther
 * Assignment 3
 * Computer Graphics
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import renderer.scene.*;
import renderer.models.*;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.event.*;
import java.awt.BorderLayout;
import javax.swing.*;
import java.util.*;
import java.util.ArrayList;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

import java.math.*;
import renderer.pipeline.Pipeline;

import java.io.*;
import java.lang.*;

/**

*/
public class Hw3 implements KeyListener, ComponentListener, MouseListener, MouseMotionListener {
   /**
    * This constructor instantiates the Scene object and initializes it with
    * appropriate geometry.
    */
   FrameBufferPanel fbp = null; // The event handlers need
   private Scene scene = null; // access to these fields.
   final JFrame jf = new JFrame("Hw3");
   private double deltaX = 0.0;
   private double deltaY = 0.0;
   private double deltaZ = 0.0;
   private double deltaS = 1.0;
   private double x = 0;
   private double y = 0;
   private boolean displayTransformations = false;
   private boolean released = false;
   private boolean hitted = false;
   private boolean mouseInfo = false;

   boolean[] hitBools = new boolean[5];

   public Hw3() {
      // Create the Scene object that we shall render
      scene = new Scene();
      // Create several Model objects.
      scene.addModel(new Square(1));
      scene.addModel(new Square(2));
      scene.addModel(new Square(3));
      scene.addModel(new Circle(3, 4));
      scene.addModel(new Circle(3, 64));

      // Give each model a useful name.
      scene.modelList.get(0).name = "Square_1";
      scene.modelList.get(1).name = "Square_2";
      scene.modelList.get(2).name = "Square_3";
      scene.modelList.get(3).name = "Diamond";
      scene.modelList.get(4).name = "Circle";

      final int fbWidth = 1000;
      final int fbHeight = 1000;

      fbp = new FrameBufferPanel(fbWidth, fbHeight);

      // Push the models away from where the camera is.
      for (Model m : scene.modelList) {
         moveModel(m, 0, 0, -10);
      }

      Rasterize_Clip.doClipping = false;

      // Give each model an initial position in the scene.
      moveModel(scene.modelList.get(0), 0, 0, 0);
      moveModel(scene.modelList.get(1), -5, -5, 0);
      moveModel(scene.modelList.get(2), +5, +5, 0);
      moveModel(scene.modelList.get(3), +5, -5, 0);
      moveModel(scene.modelList.get(4), -5, +5, 0);

      // Define initial dimensions for a FrameBuffer.
      // Create a FrameBufferPanel that holds a FrameBuffer.
      // Create a JFrame that will hold the FrameBufferPanel.
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // Place the FrameBufferPanel in the JFrame.
      jf.getContentPane().add(fbp, BorderLayout.CENTER);
      jf.pack();
      jf.setVisible(true);
      jf.add(fbp);
      jf.addKeyListener(this);
      jf.addComponentListener(this);
      fbp.addMouseMotionListener(this);
      fbp.addMouseListener(this);

      // Render.
      FrameBuffer fb = fbp.getFrameBuffer();
      fb.clearFB();
      Pipeline.render(scene, fb);
      fbp.update();

   }

   // Implement the KeyListener interface.
   @Override
   public void keyPressed(KeyEvent e) {
   }

   @Override
   public void keyReleased(KeyEvent e) {

   }

   @Override
   public void keyTyped(KeyEvent e) {
      final char c = e.getKeyChar();
      if ('h' == c) {
         print_help_message();
         return;
      } else if ('d' == c) {
         Pipeline.debug = !Pipeline.debug;
         // Rasterize_Clip.debug = !Rasterize_Clip.debug;
      } else if ('i' == c) {
         mouseInfo = true;
         System.out.println("mouse toggle info");
         return;
      } else if ('c' == c) {
         System.out.println("clipping");
         Rasterize_Clip.doClipping = !Rasterize_Clip.doClipping;
         System.out.print("Clipping is turned ");
         System.out.println(Rasterize_Clip.doClipping ? "On" : "Off");
         // return;

      } else if ('r' == c) {
         System.out.println("window size");
         int h = fbp.getHeight();
         int w = fbp.getWidth();
         FrameBuffer fb = null;
         if (w < h) {
            fb = new FrameBuffer(w, w);
         } else if (h < w) {
            fb = new FrameBuffer(h, h);
         }

         fbp.setFrameBuffer(fb);
         Pipeline.render(scene, fb);
         fbp.update();
         jf.pack();

         return;

      } else if ('R' == c) {
         System.out.println("window size2");
         int h = fbp.getHeight();
         int w = fbp.getWidth();
         FrameBuffer fb = null;
         if (w > h) {
            fb = new FrameBuffer(w, w);
         } else if (h > w) {
            fb = new FrameBuffer(h, h);
         }

         fbp.setFrameBuffer(fb);
         Pipeline.render(scene, fb);
         fbp.update();
         jf.pack();
         return;

      }
   }

   // Implement the MouseListener interface.

   @Override
   public void mouseEntered(MouseEvent e) {
   }

   // implement
   @Override
   public void mouseExited(MouseEvent e) {
   }

   // *implement */

   // *implement
   @Override
   public void mouseReleased(MouseEvent e) {

      released = true;
      hitted = false;
      // arrays.fill??
      Arrays.fill(hitBools, hitted);
   }

   @Override
   public void mouseClicked(MouseEvent e) {
   }

   // Implement the MouseMotionListener interface.

   @Override
   public void mouseMoved(MouseEvent e) {
   }

   @Override
   public void mousePressed(MouseEvent e) {

      x = getCameraX(fbp.getWidth(), e.getX());
      y = getCameraY(fbp.getHeight(), e.getY());

      double printX = Math.round(x * 100000) / 100000.0d;
      double printY = Math.round(y * 100000) / 100000.0d;

      for (int i = 0; i <= scene.modelList.size() - 1; i++) {
         if (Hit(e, scene.modelList.get(i)) == true) {
            hitted = true;
            if (mouseInfo == true) {
               System.out.println(scene.modelList.get(i).name);
            }
         } else if (Hit(e, scene.modelList.get(i)) == false) {
            hitted = false;
         }
         hitBools[i] = hitted;
      }

      if (mouseInfo == true) {
         System.out.println(e.getX() + ", " + e.getY());
         System.out.println("(x,y,z) = (    " + printX + "    " + printY + "    -10.00000)");
      }
   }

   // implement
   @Override
   public void mouseDragged(MouseEvent e) {
      // Render again.

      deltaX = x - getCameraX(fbp.getWidth(), e.getX());
      deltaY = y - getCameraY(fbp.getHeight(), e.getY());
      x = getCameraX(fbp.getWidth(), e.getX());
      y = getCameraY(fbp.getHeight(), e.getY());
      if (mouseInfo == true) {
         System.out.println("mouseDeltaX = " + (int) Math.round(-deltaX) + "mouseDeltaY = " + -deltaY);
      }
      for (int i = 0; i <= scene.modelList.size() - 1; i++) {
         if (hitBools[i] == true) {
            moveModel(scene.modelList.get(i), -deltaX, -deltaY, 0);
            if (mouseInfo == true) {
               System.out.println(scene.modelList.get(i).name);
            }
         }
      }
      final FrameBuffer fb = fbp.getFrameBuffer();
      fb.clearFB();
      Pipeline.render(scene, fb);
      fbp.update();
   }

   // Implement the ComponentListener interface.
   @Override
   public void componentMoved(ComponentEvent e) {
   }

   @Override
   public void componentHidden(ComponentEvent e) {
   }

   @Override
   public void componentShown(ComponentEvent e) {
   }

   // component resized
   @Override
   public void componentResized(ComponentEvent e) {
      // Get the new size of the FrameBufferPanel.
      final int w = fbp.getWidth();
      final int h = fbp.getHeight();

      // Create a new FrameBuffer that fits the FrameBufferPanel.
      final FrameBuffer fb = new FrameBuffer(w, h);
      fbp.setFrameBuffer(fb);
      Pipeline.render(scene, fb);
      fbp.update();

   }// comp resized

   /**
    * Create an instance of this class which has the affect of creating the GUI
    * application.
    */
   public static void main(String[] args) {
      print_help_message();
      // We need to call the program's constructor in the
      // Java GUI Event Dispatch Thread, otherwise we get a
      // race condition between the constructor (running in
      // the main() thread) and the very first ComponentEvent
      // (running in the EDT).
      javax.swing.SwingUtilities.invokeLater(() -> new Hw3() // a lambda expression
      );
   }// main

   private static void moveModel(Model m, double deltaX, double deltaY, double deltaZ) {
      for (int i = 0; i < m.vertexList.size(); ++i) {
         final Vertex v = m.vertexList.get(i);
         m.vertexList.set(i, new Vertex(v.x + deltaX, v.y + deltaY, v.z + deltaZ));
      }
   }// move model

   private static void print_help_message() {
      System.out.println("Use the 'd' key to toggle renderer debugging information on and off.");
      System.out.println("Use the 'i' key to toggle mouse debugging information on and off.");
      System.out.println("Use the 'c' key to toggle line clipping on and off.");
      System.out.println("Use the 'r/R' keys to reset the window's aspect ratio.");
      System.out.println("Use the 'h' key to redisplay this help message.");
   }// help message

   private double getCameraX(int w, int fx) {
      double xp = ((-2.001 / w * ((w / 2) - (fx + 1)))) * 10;
      return xp;
   }// get camera X

   private double getCameraY(int h, int fy) {
      double yp = ((-2.001 / h * ((h / 2) - (h - fy)))) * 10;
      return yp;
   }

   private boolean Hit(MouseEvent e, Model m) {
      // circle
      double v0x = m.vertexList.get(0).x;
      double v0y = m.vertexList.get(0).y;
      double v3x = m.vertexList.get(2).x;
      double v3y = m.vertexList.get(2).y;
      double mpx = (v0x + v3x) / 2;
      double mpy = (v0y + v3y) / 2;
      // distance from center to a line segment
      double distanceX = Math.abs((v3x - v0y) / 2);
      // double distanceY = Math.abs((v0y + v3y) / mpy);
      double distanceY = Math.abs((v3y - v0y) / 2);
      boolean didHit = false;
      double cx = getCameraX(fbp.getWidth(), e.getX());
      double cy = getCameraY(fbp.getHeight(), e.getY());
      double centerX = scene.modelList.get(4).vertexList.get(0).x - 3;
      double centerY = scene.modelList.get(4).vertexList.get(0).y;

      if (m.name == "Square_1") {
         // square
         if ((cx - mpx >= -(distanceX) && (cx - mpx) <= distanceX)
               && (cy - mpy >= -(distanceY) && (cy - mpy) <= distanceY)) {
            didHit = true;
            return didHit;
         } else {
            didHit = false;
         }

      }
      if (m.name == "Square_2") {
         if ((cx - mpx >= -(distanceX) && (cx - mpx) <= distanceX)
               && (cy - mpy >= -(distanceY) && (cy - mpy) <= distanceY)) {
            didHit = true;
            return didHit;
         } else {
            didHit = false;
         }
      }
      if (m.name == "Square_3") {
         if ((cx - mpx >= -(distanceX) && (cx - mpx) <= distanceX)
               && (cy - mpy >= -(distanceY) && (cy - mpy) <= distanceY)) {
            didHit = true;
            return didHit;
         } else {
            didHit = false;
         }
      }
      if (m.name == "Circle") {
         if (Math.pow(cx - (centerX), 2) + Math.pow(cy - centerY, 2) < Math.pow(3, 2)) {
            didHit = true;
         } else {
            didHit = false;
         }
         return didHit;
      }
      // diamond
      if (m.name == "Diamond") {
         centerX = scene.modelList.get(3).vertexList.get(0).x - 3;
         centerY = scene.modelList.get(3).vertexList.get(0).y;
         if ((Math.pow(cx - centerX, 2)) / 9 + (Math.pow(cy - (centerY), 2)) / 9 < 1) {
            didHit = true;
         } else {
            didHit = false;
         }
         return didHit;
      }
      return didHit;
   }
}
