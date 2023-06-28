package com.mobiquity.packer;

import com.mobiquity.controller.PackerRestController;
import com.mobiquity.entity.GiftPackage;
import com.mobiquity.exception.APIException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
public class Packer {
  private Packer() {
  }

  private static final int WEIGHT_LIMIT = 100;
  private static final int NUM_ITEMS=15;
  private static final int MAX_COST=100;

//get the gift package with defined attributes
  private static List<GiftPackage> processGiftPackageItems(String listItems) throws APIException {
    List<GiftPackage> items = new ArrayList<>();
    String[] itemStrings = listItems.split(" ");
    for (String itemString : itemStrings) {
      String[] parts = itemString.replaceAll("[()€]", "").split(",");
      int index = Integer.parseInt(parts[0]);
      double weight = Double.parseDouble(parts[1]);
      double cost = Double.parseDouble(parts[2]);
      if(cost>MAX_COST){
        throw new APIException("Cost of an item is more than limit cant proceed"+cost);
      }
      items.add(new GiftPackage(index,weight,cost));
    }
    return items;
  }
//dynamic programming logic to find out possible combinations
  private static int[][] getPermutations(int weightLimit,List<GiftPackage> items){
    int[][] k = new int[items.size() + 1][weightLimit + 1];
    for (int i = 1; i <= items.size(); i++) {
      GiftPackage item = items.get(i - 1);
      for (int j = 1; j <= weightLimit; j++) {
        if (item.getWeight() > j) {
          k[i][j] = k[i - 1][j];
        } else {
          k[i][j] = (int) Math.max(k[i - 1][j], k[i - 1][(int) (j - item.getWeight())] + item.getCost());
        }
      }
    }
    return k;
  }

  //Get the possible solution of items to create gift package
  public static String pack(String filePath) throws APIException {

    Path fPath = Paths.get(filePath);
    StringBuilder finalResult = new StringBuilder();
    try(Stream<String> lines = Files.lines(fPath)){
          for(String s : lines.toList()){
            String[] splitParts = s.split(" : ");
            int weightLimit = Integer.parseInt(splitParts[0]);
            if(weightLimit > WEIGHT_LIMIT){
              throw new APIException("Maximum Weight Limit Reached..cant proceed");
            }
            List<GiftPackage> packages = processGiftPackageItems(splitParts[1]);
            if(packages.size()>NUM_ITEMS){
              throw new APIException("Maximum Items Limit Reached..cant proceed");
            }

            //We have to find packages that are all together has total weight less than or equal to weightLimit
            //compare weightLimit with provided weight limit if not matching then throw an exception
            if(weightLimit==PackerRestController.theweightLimit){
              int dp[][]=getPermutations(weightLimit,packages);
              //now we have to get index numbers from the possible combinations array
              String result = getPossibleCombinationItems(dp,packages,weightLimit);
              finalResult.append(result.concat("\n"));
            }

          }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return finalResult.toString();
  }
//get possible combinations to create package
  private static String getPossibleCombinationItems(int[][] dp, List<GiftPackage> packages, int weightLimit) {
    int maxCost = dp[packages.size()][weightLimit];
    StringBuilder result = new StringBuilder();
    for (int i = packages.size(); i > 0 && maxCost > 0; i--) {
      if (maxCost != dp[i - 1][weightLimit]) {
        GiftPackage item = packages.get(i - 1);
        result.insert(0, item.getIndexNumber() + ",");
        maxCost -= item.getCost();
        weightLimit -= item.getWeight();
      }
    }
    if (result.length() > 0) {
      result.setLength(result.length() - 1);
    } else {
      result.append("-");
    }
    return result.toString();
  }
}
