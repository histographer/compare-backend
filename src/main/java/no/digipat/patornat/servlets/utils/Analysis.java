package no.digipat.patornat.servlets.utils;

import no.digipat.patornat.models.image.Image;
import no.digipat.patornat.models.image.ImageComparison;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Analysis {


    public static JSONObject createRequestJson(List<Image> images, List<ImageComparison> comparisons) {
        JSONObject json = new JSONObject();
        List<Long> imageIds = new ArrayList<>();
        for (Image image : images) {
            imageIds.add(image.getId());
        }
        json.put("image_ids", imageIds);
        List<JSONObject> jsonComparisons = new ArrayList<>();
        for (ImageComparison comparison : comparisons) {
            JSONObject comparisonJson = new JSONObject();
            JSONObject winnerJson = new JSONObject();
            winnerJson.put("id", comparison.getChosen().getId());
            JSONObject loserJson = new JSONObject();
            loserJson.put("id", comparison.getOther().getId());
            comparisonJson.put("winner", winnerJson);
            comparisonJson.put("loser", loserJson);
            jsonComparisons.add(comparisonJson);
        }
        json.put("comparison_data", jsonComparisons);
        System.out.println(json.toString());
        return json;
    }

}
