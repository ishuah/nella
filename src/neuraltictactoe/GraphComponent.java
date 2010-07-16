/***
 * Neuroph  http://neuroph.sourceforge.net
 * Copyright by Neuroph Project (C) 2008
 *
 * This file is part of Neuroph framework.
 *
 * Neuroph is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Neuroph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Neuroph. If not, see <http://www.gnu.org/licenses/>.
 */

package neuraltictactoe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Ivan Trnavac
 * @author Zoran Sevarac
 */
public class GraphComponent extends javax.swing.JComponent implements Runnable {

    private Image imageBuffer;
    private Graphics2D graphicsBuffer;
    private Vector<Point2D> pointsBuffer;
    private int bufferSize = 100000;
    private Point2D point2d;
    private Point point0 = null;
    private GraphScope scope;
    private GraphScope defaultScope;
    private GraphView view;
    private int w,  h;
    private int sw,  sh; //scope width and height
    private int maxSize = 1700000;
    private Rectangle rec = null;
    private Point mousePoint = null;
    private int zw;
    private int zh; //zoom rectangle width and height
    private boolean zoomOn = true;
    private boolean isPainting = false;
    private int r = 2; //point radius
    private int zoomMode = 0; //0, 1 = fix width, 2 = fix height

    /* Creates new form GraphComponent */
    
    public GraphComponent() {
        initComponents();
        scope = new GraphScope();
        defaultScope = scope.clone();
        view = new GraphView();
        pointsBuffer = new Vector<Point2D>();
        setSize();

        addPoint(new Point.Double(0, 0));
    }

    public void setZoomMode(int mode) {
        zoomMode = mode;
    }

    public void setZoomOn(boolean zoomOn) {
        this.zoomOn = zoomOn;
    }

    private void setSize() {
        sw = scope.calculateWidth();
        sh = scope.calculateHeight();
        w = sw + view.getWestGap() + view.getEastGap();
        h = sh + view.getNorthGap() + view.getSouthGap();
        setSize(w, h);
        setPreferredSize(new Dimension(w, h));
        /*Component c = getParent();
        if (c != null) {
        c.setSize(w, h);
        c.setPreferredSize(new Dimension(w, h));
        }*/
        zw = 50;
        zh = zw;
    }

    public void resizeMaxX(double value) {
        scope.setMaxX(scope.getMaxX() * value);
        scope.setFixedSize(w - view.getHGap(), h - view.getWestGap());
        defaultScope = scope.clone();
        redraw();
        repaint();
    }

    public void resizeMaxY(double value) {
        scope.setMaxY(scope.getMaxY() * value);
        scope.setFixedSize(w - view.getHGap(), h - view.getWestGap());
        defaultScope = scope.clone();
        redraw();
        repaint();
    }

    public void zoomX(double value) {
        if (value < 1) {
            if (w * h * (2 - value) > maxSize) {
                return;
            }
        }
        scope.zoomX(value);
        if (scope.calculateWidth() + view.getVGap() < defaultScope.calculateWidth()) {            
            resizeMaxX(value);
            return;
        }
        clearGraphics();
        setSize();
        repaint();
    }

    public void zoomY(double value) {
        if (value < 1) {
            if (w * h * (2 - value) > maxSize) {
                return;
            }
        }
        scope.zoomY(value);
        clearGraphics();
        setSize();
        repaint();
    }

    public GraphScope getScope() {
        return scope;
    }

    public void setScope(GraphScope graphScope) {
        scope = graphScope;
        scope.setFixedSize(sw, sh);
        clearGraphics();
        setSize();
        repaint();   
    }

    public GraphView getView() {
        return view;
    }

    public void setView(GraphView graphView) {
        view = graphView;
        clearGraphics();
        setSize();
        //redraw();
        repaint();
    }

    public void addPoint(Point2D p) {
        if (p == null) {
            return;
        }
        point2d = p;
        if (pointsBuffer.size() >= bufferSize) {            
            pointsBuffer.remove(0);
        }
        pointsBuffer.add(p);
        if (!repaintGraph()) {
           System.out.print(" ");
        }
    }

    public void addPoints(Vector<Point2D> v) {
        if (v.size() == 0) {
            return;
        }
        pointsBuffer = v;

        Point2D pMaxX = v.get(0);
        Point2D pMaxY = v.get(0);
        for (int i = 1; i < v.size(); i++) {
            if (v.get(i).getX() > pMaxX.getX()) {
                pMaxX = v.get(i);
            }
            if (v.get(i).getY() > pMaxY.getY()) {
                pMaxY = v.get(i);
            }
        }
        Point pX = scope.getPoint(pMaxX, view.getWestGap(), view.getSouthGap());
        Point pY = scope.getPoint(pMaxY, view.getWestGap(), view.getSouthGap());
        //provera da li je van okvira if (p.y > getHeight()-northGap) return;
        boolean ret = false;
        if (pX.x > w - view.getEastGap()) {
            scope.setMaxX(pMaxX.getX());
            //resizeMaxX(1.5);
            ret = true;
        }
        if (pY.y > h - view.getNorthGap()) {
            scope.setMaxY(pMaxY.getY());
            //resizeMaxY(1.5);
            ret = true;
        }
        if (ret) {
            scope.setFixedSize(w - view.getHGap(), h - view.getVGap());
            defaultScope = scope.clone();
        }    
        redraw();
        repaint();
    }

    private synchronized void reduceBuffer() {
        if (pointsBuffer.size() == bufferSize) {
            for (int i = 0; i < getBufferSize() * 0.2; i++) {
                pointsBuffer.remove(i);
            }
        }
    }

    public boolean repaintGraph() {
        if (isPainting) {
            return false;
        }
        repaint();
        return true;
    }

    @Override
    public void paintComponent(Graphics g) {
        isPainting = true;
        draw();
        g.drawImage(imageBuffer, 0, 0, this);
        isPainting = false;
    }

    private void draw() {
        if (createGraphics()) {
            drawBackground();
            drawNumbers();
            drawAxis();
            drawEntireGraph();
        } else {
            drawPoint();
        }
        if (zoomOn) {
            drawZoom();
        }
    }

    private boolean createGraphics() {
        if (graphicsBuffer == null) {
            point2d = null;
            point0 = null;
            imageBuffer = createImage(w, h);
            graphicsBuffer = (Graphics2D) imageBuffer.getGraphics();
            graphicsBuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            return true;
        }
        return false;
    }

    private void drawBackground() {
        graphicsBuffer.setColor(view.getBackground());
        graphicsBuffer.fillRect(0, 0, w, h);
        graphicsBuffer.setColor(view.getFrameColor());
        graphicsBuffer.drawRect(view.getWestGap(), view.getNorthGap(), sw, sh);
    }

    private void drawAxis() {
        graphicsBuffer.setColor(view.getAxisColor());
        int x = scope.calculateX0() + view.getWestGap();//+1;
        int y = h - scope.calculateY0() /*+ 1*/ - view.getSouthGap();
        if (x < view.getWestGap()) {
            //Axis X
            graphicsBuffer.drawLine(view.getWestGap(), y, w - view.getEastGap(), y);
            return;
        }
        //Axis X
        graphicsBuffer.drawLine(view.getWestGap(), y, w - view.getEastGap(), y);
        //Axis Y
        graphicsBuffer.drawLine(x, view.getNorthGap(), x, h - view.getSouthGap());
    }

    private void drawPoint() {
        if (point2d != null) {
            Point p = scope.getPoint(point2d, view.getWestGap(), view.getSouthGap());
            //provera da li je van okvira if (p.y > getHeight()-northGap) return;
            boolean ret = false;
            if (p.x > w - view.getEastGap()) {
                resizeMaxX(1.5);                
                ret = true;
            }
            if (p.y > h - view.getNorthGap()) {
                resizeMaxY(1.5);
                ret = true;
            }
            if (ret) {
                return;
            }

            if (p != null) {
                if (view.isVisiblePoints()) {
                    graphicsBuffer.setColor(view.getPointColor());
                    //graphicsBuffer.drawOval(p.x - r, h - p.y - r, r*2, r*2);
                    graphicsBuffer.drawRect(p.x - r, h - p.y - r, r * 2, r * 2);
                }
                if (!view.isVisibleLine()) {
                    return;
                }
                if (point0 != null) {
                    graphicsBuffer.setColor(view.getLineColor());
                    graphicsBuffer.drawLine(point0.x, getHeight() - point0.y, p.x, getHeight() - p.y);
                }
                point0 = p;
            } else {
                point0 = null;
            }
        }
    }

    private void drawEntireGraph() {
        Vector<Point2D> v = getPointsBuffer();
        if (v != null && v.size() > 0) {
            Point p1 = null;
            for (int i = 0; i < v.size(); i++) {
                Point p = scope.getPoint(v.get(i), view.getWestGap(), view.getSouthGap());
                if (p != null) {
                    if (view.isVisibleLine() && p1 != null) {
                        graphicsBuffer.setColor(view.getLineColor());
                        graphicsBuffer.drawLine(p1.x, h - p1.y, p.x, h - p.y);
                    }
                    if (view.isVisiblePoints()) {
                        graphicsBuffer.setColor(view.getPointColor());
                        //graphicsBuffer.drawOval(p.x - r, h - p.y - r, r*2, r*2);
                        graphicsBuffer.drawRect(p.x - r, h - p.y - r, r * 2, r * 2);
                    }
                }
                p1 = p;
            }
        }
    }

    private void drawZoom() {
        graphicsBuffer.setXORMode(Color.white);
        graphicsBuffer.setColor(Color.black);
        if (mousePoint == null) {
            if (rec != null) {
                graphicsBuffer.drawRect(rec.x, rec.y, rec.width, rec.height);
                rec = null;
            }
            graphicsBuffer.setPaintMode();
            return;
        }
        if (rec != null) {
            graphicsBuffer.drawRect(rec.x, rec.y, rec.width, rec.height);

        }
        switch (zoomMode) {
            case 0:
                rec = new Rectangle(mousePoint.x - zw / 2, mousePoint.y - zh / 2, zw, zh);
                break;
            case 1:
                rec = new Rectangle(view.getWestGap(), mousePoint.y - zh / 2, w - view.getHGap(), zh);
                break;
            case 2:
                rec = new Rectangle(mousePoint.x - zw / 2, view.getNorthGap(), zw, h - view.getVGap());
                break;
        }
        graphicsBuffer.drawRect(rec.x, rec.y, rec.width, rec.height);
        graphicsBuffer.setPaintMode();
    }

    public void redraw() {
        drawBackground();
        drawNumbers();
        drawAxis();
        drawEntireGraph();
    }

    private void clearGraphics() {
        if (graphicsBuffer != null) {
            graphicsBuffer.clearRect(0, 0, w, h);
        }
        graphicsBuffer = null;
    }

    public void clearBuffer() {
        getPointsBuffer().clear();
    }

    public void clear() {
        clearGraphics();
        clearBuffer();
        repaint();
    }

    private void drawNumbers() {
        Stroke stroke1 = graphicsBuffer.getStroke();
        float[] dashPattern = {1, 3};
        Stroke stroke2 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0);
        graphicsBuffer.setStroke(stroke2);
        graphicsBuffer.setFont(new Font("Arial", Font.PLAIN, 10)); //Font f; f.getSize()
        //graphicsBuffer.setColor(foreground);
        double numX = scope.getMinX();
        while (numX <= scope.getMaxX()) {
            Point p = scope.getPoint(new Point2D.Double(numX, scope.getMinY()), view.getWestGap(), view.getSouthGap());
            graphicsBuffer.setColor(view.getNumColor());
            graphicsBuffer.drawString(view.getDecimalFormatX().format(numX), p.x - 10, h - p.y + 10);
            /*graphicsBuffer.drawLine(p.x, getHeight() - southGap, p.x, getHeight() - southGap - 5);
            graphicsBuffer.drawLine(p.x, northGap, p.x, northGap + 5);*/
            if (view.isVisibleGrid()) {
                graphicsBuffer.setColor(view.getGridColor());
                graphicsBuffer.drawLine(p.x, view.getNorthGap(), p.x, h - view.getSouthGap());//vertical
            }
            //numX += view.getNumPeriodX();
            numX = (double) (Math.round(numX + view.getFixPixNumPeriodX() * scope.getPixValueX()) + 1);
        }
        double numY = scope.getMinY();
        while (numY <= scope.getMaxY()) {
            Point p = scope.getPoint(new Point2D.Double(0, numY), view.getWestGap(), view.getSouthGap());
            //Point p = scope.getPoint(new Point2D.Double(scope.getMinX(), numY), view.getWestGap(), view.getSouthGap());
            graphicsBuffer.setColor(view.getNumColor());
            graphicsBuffer.drawString(view.getDecimalFormatY().format(numY), view.getWestGap() - 25, h - p.y + 3);

            //graphicsBuffer.drawLine(westGap, getHeight() - p.y, westGap + 5, getHeight() - p.y);
            if (view.isVisibleGrid()) {
                graphicsBuffer.setColor(view.getGridColor());
                graphicsBuffer.drawLine(view.getWestGap(), h - p.y, w - view.getEastGap(), h - p.y);//horizontal
            }
           numY += view.getNumPeriodY();                        
            //numY = Double.parseDouble(view.getDecimalFormatY().format(numY + view.getFixPixNumPeriodY() * scope.getPixValueY()));
        }
        graphicsBuffer.setStroke(stroke1);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
    }// </editor-fold>//GEN-END:initComponents
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        if (!zoomOn) {
            return;
        }
        if (rec == null) {
            return;
        }
        if (evt.getButton() == MouseEvent.BUTTON1) {
            scope.crop(new Point(rec.x + rec.width, h - rec.y), new Point(rec.x, h - (rec.y + rec.height)),
                    view.getWestGap(), view.getSouthGap());
            scope.setFixedSize(w - view.getHGap(), h - view.getVGap());
            setSize();
            clearGraphics();
            //ili
            //redraw();
            rec = null;
            repaint();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            rec = null;
            setScope(defaultScope.clone());
        }       
    }//GEN-LAST:event_formMousePressed

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        if (!zoomOn) {
            return;
        }
        double v = 1;
        switch (evt.getWheelRotation()) {
            case -1:
                v = 0.91;
                break;
            case 1:
                v = 1.1;
                break;

        }
        switch (zoomMode) {
            case 0:
                zw *= v;
                zh *= v;
                break;
            case 1:
                zh *= v;
                break;
            case 2:
                zw *= v;
                break;

        }
        repaint();
    }//GEN-LAST:event_formMouseWheelMoved

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        if (zoomOn) {
            mousePoint = evt.getPoint();
            repaint();
        }
    }//GEN-LAST:event_formMouseMoved

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        if (zoomOn) {
            mousePoint = null;
            repaint();
        }
    }//GEN-LAST:event_formMouseExited

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /*public void update(Observable o, Object arg) {
        LMS learningRule = (LMS) o;
        Point2D p = new Point2D.Double(learningRule.getCurrentIteration(), learningRule.getTotalNetworkError());
        dataQueue.add(p);
        if (!working) {
            thread = new Thread(this);
            thread.start();
        }
        System.out.println(p);
    }*/
    
    public void addConcurrentPoint(Point2D p) {
        dataQueue.add(p);
        if (!working) {
            thread = new Thread(this);
            thread.start();
        }
    }
    // *** Thread ***
    private static boolean working = false;
    private Thread thread = null;
    private ConcurrentLinkedQueue<Point2D> dataQueue = new ConcurrentLinkedQueue<Point2D>();

    public void run() {
        working = true;
        //boolean wasZoomOn = zoomOn;
        //setZoomOn(false);
        while (dataQueue.size() > 0) {
            if (dataQueue.isEmpty()) {
                try {
                    Thread.sleep(2);                    
                } catch (InterruptedException ex) {                   
                }
            } else {
                addPoint(dataQueue.poll());                
            }
        }
        //System.out.println("Interrupted " + getPointsBuffer().size());
        //setZoomOn(wasZoomOn);
        working = false;
    }

    public boolean isWorking() {
        return working;
    }
    
    public void startThread() {          
        if (!working) {
            thread = new Thread(this);
            thread.start();
        }        
    }

    public void stopThread() {        
        if (!dataQueue.isEmpty()) {
            dataQueue.clear();
        }        
    }

    public Vector<Point2D> getPointsBuffer() {
        return pointsBuffer;
    }    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
