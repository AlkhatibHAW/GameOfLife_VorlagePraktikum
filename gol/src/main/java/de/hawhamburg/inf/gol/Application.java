package de.hawhamburg.inf.gol;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Main application class.
 * 
 * @author Christian Lins
 */
public class Application {

    /* Size of the playground in X dimension */
    public static final int DIM_X = 200;
    
    /* Size of the playground in Y dimension */
    
    public static final int DIM_Y = 200;
    
    /* Probability threshold that a cell is initially being created */
    public static final float ALIVE_PROBABILITY = 0.3125f;
    
    /* Sleep time between every generation in milliseconds */
    public static final int SLEEP = 200;
    
    /**
     * Creates an potentially unlimited stream of Cell objects. The stream uses
     * random numbers between [0, 1] and the probability threshold whether a
     * cell is created DEAD (random > p) or ALIVE (random <= p).
     * 
     * @param p Cell alive probability threshold.
     * @return 
     */
    private static Stream<Cell> createCellStream(float p) {
        // TODO
        Random random = new Random();
        Stream<Cell> stream = Stream.generate(() -> random.nextFloat() >= p ? new Cell(1): new Cell(0));
        
        return stream;
    }
    
    public static void main(String[] args) {
        Stream<Cell> cellStream = createCellStream(ALIVE_PROBABILITY);
        Playground playground = new Playground(DIM_X, DIM_Y, cellStream);
        
        // Create and show the application window
        ApplicationFrame window = new ApplicationFrame();
        window.setVisible(true);
        window.getContentPane().add(new PlaygroundComponent(playground));
        
        // Create and start a LifeThreadPool with 50 threads
        LifeThreadPool pool = new LifeThreadPool(50);
        pool.start();
        
        while (true) {
            Life life = new Life(playground);
            for (int xi = 0; xi < DIM_X; xi++) {
                for (int yi = 0; yi < DIM_Y; yi++) {
                                       
                    // Submit new life.process() call as runable to the pool
                    // TODO
                    int finalXi = xi;
                    int finalYi = yi;
                    pool.submit(
                            () -> life.process(playground.getCell(finalXi, finalYi), finalXi, finalYi));
                    
                }
            }

            // Wait for all threads to finish this generation
            // TODO
            try {
                pool.joinAndExit();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Submit switch to next generation for each cell and force a
            // window repaint to update the graphics
            pool.submit(() -> {
                playground.asList().forEach(cell -> cell.nextGen());
                window.validate();
                window.repaint();
            });
            pool.interrupt();
            // Wait SLEEP milliseconds until the next generation
           // TODO
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
