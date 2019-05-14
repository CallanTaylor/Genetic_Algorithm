import cosc343.assig2.Creature;
import java.util.Random;

/**
 * The MyCreate extends the cosc343 assignment 2 Creature.  Here you implement
 * creatures chromosome and the agent function that maps creature percepts to
 * actions.  
 *
 * @author  
 * @version 1.0
 * @since   2017-04-05 
 */
public class MyCreature extends Creature {

    // Random number generator
    Random rand = new Random();
    private float[] chromosome;
    private int last_move;

    private static final int CHROMOSOME_SIZE = 8;

    private static final int FEAR = 0;
    private static final int HUNGER = 1;
    private static final int CURIOSITY = 2;
    private static final int CAUTION = 3;
    private static final int EXPLORE = 4;
    private static final int BOREDOM = 5;
    
    private static final int EAT_RED = 6;
    private static final int EAT_GREEN = 7;


    /* default constructor for MyCreature
     */
    public MyCreature(int numPercepts, int numActions) {
        this.chromosome = new float[CHROMOSOME_SIZE];

        for (int i = 0; i < CHROMOSOME_SIZE; i++) {
            this.chromosome[i] = rand.nextFloat();
        }
    }


    /* Constructor for MyCearure that creates new instance our of
       two parents from the previous generation
     */
    public MyCreature(MyCreature mother, MyCreature farther) {
        this.chromosome = new float[CHROMOSOME_SIZE];

        int switch_point = rand.nextInt(CHROMOSOME_SIZE - 1) + 1;
       

        for (int i = 0; i < switch_point; i++) {
            if (rand.nextFloat() < 0.05) {
                this.chromosome[i] = rand.nextFloat();
            } else {
                this.chromosome[i] = new_gene(mother.chromosome[i]);
            }
                                              
        }
       
        for (int i = switch_point; i < CHROMOSOME_SIZE; i++) {
            if (rand.nextFloat() < 0.05) {
                this.chromosome[i] = rand.nextFloat();
            } else {
                this.chromosome[i] = new_gene(farther.chromosome[i]);
            }
        }
    }


    /* The agent function is responsible for mapping precepts to actions using the
       creatures chromosome.
     */
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) {

        float[]actions = new float[numExpectedActions];


        for (int i = 0; i < numPercepts; i++) {


            if (i != 4) {

                // Explore empty tiles
                if (percepts[i] == 0) {
                    actions[i] += chromosome[EXPLORE];
                }

                // Fear mosters
                if (percepts[i] == 1) {
                    actions[i] -= chromosome[FEAR];

                    // Caution is mapped to tiles adjacent to a moster
                    if (i == 0) {
                        actions[i + 1] -= chromosome[CAUTION];
                        actions[i + 3] -= chromosome[CAUTION];
                    } else if (i == 1 || i == 7) {
                        actions[i - 1] -= chromosome[CAUTION];
                        actions[i + 1] -= chromosome[CAUTION];
                    } else if (i == 2) {
                        actions[i - 1] -= chromosome[CAUTION];
                        actions[i + 3] -= chromosome[CAUTION];
                    } else if (i == 3 || i == 5) {
                        actions[i - 3] -= chromosome[CAUTION];
                        actions[i + 3] -= chromosome[CAUTION];
                    } else if (i == 6) {
                        actions[i - 3] -= chromosome[CAUTION];
                        actions[i + 1] -= chromosome[CAUTION];
                    } else if (i == 8) {
                        actions[i - 3] -= chromosome[CAUTION];
                        actions[i - 1] -= chromosome[CAUTION];
                    }
     
                }

                // Hunger for strawberries
                if (percepts[i] == 3) {
                    actions[i] += chromosome[HUNGER];
                }
            }

            else {

                // Desire to eat unripe food
                if (percepts[i] == 1) {
                    actions[9] = chromosome[EAT_GREEN];
                }

                // Desire to eat ripe food
                if (percepts[4] == 2) {
                    actions[9] = chromosome[EAT_RED];
                }
            }
        }

        // If last action was a movement then subtract
        // from the desire to return to same tile
        if (last_move < 9) {
            actions[8 - last_move] -= chromosome[BOREDOM];
        }
        

        // Memory
        
        actions[10] = chromosome[CURIOSITY];

        int move = 0;
        float max = 0;
        for (int i = 0; i < numExpectedActions; i++) {
            if (actions[i] > max) {
                max = actions[i];
                move = i;
            }
        }

        last_move = move;


        return actions;
    }

    /* create a new gene within %10 range of old
     */
    public float new_gene(float seed) {

        float min = (seed - (seed / 10));
        float max = (seed + (seed / 10));
        
        float new_gene = min + rand.nextFloat() * (max - min);

        return new_gene;
    }
}
