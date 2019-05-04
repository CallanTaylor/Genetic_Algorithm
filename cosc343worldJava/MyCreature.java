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

    private static final int CHROMOSOME_SIZE = 7;

    private static final int ENEMY = 0;
    private static final int FRIEND = 1;
    private static final int EMPTY = 2;
    private static final int FIND_FOOD = 3;
    private static final int EXPLORE = 4;
    private static final int EAT_GREEN = 5;
    private static final int EAT_RED = 6;

    /* Empty constructor - might be a good idea here to put the code that 
       initialises the chromosome to some random state   
  
       Input: numPercept - number of percepts that creature will be receiving
       numAction - number of action output vector that creature will need
       to produce on every turn
    */
    public MyCreature(int numPercepts, int numActions) {
        chromosome = new float[CHROMOSOME_SIZE];

        for (int i = 0; i < CHROMOSOME_SIZE; i++) {
            chromosome[i] = rand.nextFloat();
        }
    }


    
    public MyCreature(MyCreature mother, MyCreature farther) {
        chomosome = new float[CHROMOSOME_SIZE];

        for (int i = 0; i < 4; i++) {
            chromosome[i] = mother.chromosome[i];
        }

        for (int i = 4; i < 7; i++) {
            chromosome[i] = farther.chromosome[i];
        }
    }
  
    /* This function must be overridden by the MyCreature class, because it implements
       the AgentFunction which controls creature behavoiur.  This behaviour
       should be governed by the model (that you need to come up with) that is
       parameterise by the chromosome.  
  
       Input: percepts - an array of percepts
       numPercepts - the size of the array of percepts depend on the percept
       chosen
       numExpectedAction - this number tells you what the expected size
       of the returned array of percepts should bes
       Returns: an array of actions 
    */
    @Override
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) {

      
        // This is where your chromosome gives rise to the model that maps
        // percepts to actions.  This function governs your creature's behaviour.
        // You need to figure out what model you want to use, and how you're going
        // to encode its parameters in a chromosome.
      
        // At the moment, the actions are chosen completely at random, ignoring
        // the percepts.  You need to replace this code.
        float actions[] = new float[numExpectedActions];
        for(int i = 1; i < numPercepts; i++) {

            // Percept detects nothing
            if (percepts[i] == 0) {
                actions[i] = chromosome[EMPTY];
            }

            // Percept detects a moster
            if (percepts[i] == 1) {
                actions[i] = chromosome[ENEMY];
            }

            // Percept detects a friend
            if (percepts[i] == 2) {
                actions[i] = chromosome[FRIEND];
            }

            // Percept detetcts food
            if (percepts[i] == 3) {
                actions[i] = chromosome[FIND_FOOD];
            }
        }

        if (percepts[0] == 0) {
            actions[numPercepts] = 0;
        } else if (percepts[0] == 1) {
            actions[numPercepts] = chromosome[EAT_GREEN];
        } else {
            actions[numPercepts] = chromosome[EAT_RED];
        }

        actions[numExpectedActions - 1] = chromosome[EXPLORE];

          
        return actions;
    }
  
}
