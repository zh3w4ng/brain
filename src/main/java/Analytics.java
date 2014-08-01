import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by roomorama on 1/8/14.
 */
public class Analytics {

    public static void main(String[] args) {
        try {
            DataModel dm = new FileDataModel(new File("src/main/resources/u.data.csv"));
            print_evaluation(dm);
//            print_recommendations(dm);
        } catch (IOException e) {
            System.out.println("There is no such file");
            e.printStackTrace();
        } catch (TasteException e) {
            System.out.println("There is a Taste Exception");
            e.printStackTrace();
        }
    }

    protected static void print_recommendations(DataModel dm) throws TasteException {
        ItemSimilarity sim = new LogLikelihoodSimilarity(dm);
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dm, sim);
        int x = 1;
        for (LongPrimitiveIterator items = dm.getItemIDs(); items.hasNext(); ) {
            long itemId = items.nextLong();
            List<RecommendedItem> recommendations = recommender.mostSimilarItems(itemId, 5);
            for (RecommendedItem recommendation : recommendations) {
                System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
            }
            x++;
            if (x > 10) System.exit(1);
        }
    }

    protected static void print_evaluation(DataModel dm) throws TasteException {
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder builder = new RecommenderBuilder() {
            public GenericItemBasedRecommender buildRecommender(DataModel dm) {
                ItemSimilarity sim = new LogLikelihoodSimilarity(dm);
                return new GenericItemBasedRecommender(dm, sim);
            }
        };
        double result = evaluator.evaluate(builder, null, dm, 0.9, 1.0);
        System.out.println(result);
    }

}

