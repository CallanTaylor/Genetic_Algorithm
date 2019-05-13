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

    private static final int CHROMOSOME_SIZE = 5;

    private static final int FEAR = 0;
    private static final int HUNGER = 1;
    private static final int CURIOSITY = 2;
    
    private static final int EAT_RED = 3;
    private static final int EAT_GREEN = 4;


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

                if (percepts[i] == 0) {
                    actions[i] = chromosome[CURIOSITY];
                }

                if (percepts[i] == 1) {
                    actions[i] = chromosome[FEAR];
                }

                if (percepts[i] == 3) {
                    actions[i] = chromosome[HUNGER];
                }
            }

            else {

                if (percepts[i] == 1) {
                    actions[9] = chromosome[EAT_GREEN];
                }

                if (percepts[4] == 2) {
                    actions[9] = chromosome[EAT_RED];
                }
            }
        }
        
      
        
        actions[10] = chromosome[CURIOSITY];


        return actions;
    }

    public float new_gene(float seed) {

        float min = (seed - (seed / 5));
        float max = (seed + (seed / 5));
        
        float new_gene = min + rand.nextFloat() * (max - min);

        return new_gene;
    }

    public float getGene(int i) {
        return chromosome[i];
    }
}
