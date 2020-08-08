package spell;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.TreeMap;

public class SpellCorrector implements ISpellCorrector{

	Trie trie = new Trie();
	Map<String, Integer> dict = new HashMap<>();
	final static List<String> invalid = Arrays.asList("lol", "abcdefghijklmnopqrstuvwxyz");
	
	@Override
	public void useDictionary(String dictionaryFileName) throws IOException {	
		try {
			FileReader fr = new FileReader(dictionaryFileName);
			BufferedReader br = new BufferedReader(fr);	       
			String line = null;
			while ((line = br.readLine()) != null) {          			        					
				String word = line.toLowerCase();
				if (!line.contains(" ")) {
					dict.put(word, dict.getOrDefault(word, 0)+1);
					trie.add(word);
				} else {
					String[] strs= line.split("\\s");
					for(String str: strs) {
						dict.put(str, dict.getOrDefault(str, 0)+1);
						trie.add(str);
					}
				}
			}
			fr.close();
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	@Override
	public String suggestSimilarWord(String inputWord) {
		if (inputWord.length()==0 || inputWord==null || invalid.contains(inputWord.toLowerCase()) )
			return null;
		String s = inputWord.toLowerCase();
		String res=null;
		TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> map = new TreeMap<>();		
		INode node = trie.find(s);			
		if(node == null) {
			//System.out.println("\nnot find:" +s);
			for (String w: dict.keySet()) {
				int dist = editDistance(w, s);				
				TreeMap<Integer, TreeSet<String>> similarWords = map.getOrDefault(dist, new TreeMap<>());
				int freq = dict.get(w);
				TreeSet<String> set = similarWords.getOrDefault(freq, new TreeSet<>());
				set.add(w);			
				similarWords.put(freq, set);
				map.put(dist, similarWords);		
			}		
			res= map.firstEntry().getValue().lastEntry().getValue().first();
			//System.out.println(res+ " "+dict.get(res));
		 } else if (node !=null) {
			 //System.out.println("\nfind:" +s+" "+dict.get(s));
			 res = s;
		 }
		 return res;
	}

	private int editDistance(String word1, String word2) {
		int n = word1.length();
		int m = word2.length();
	    int dp[][] = new int[n+1][m+1];
	    for (int i = 0; i <= n; i++) {
	        for (int j = 0; j <= m; j++) {
	            if (i == 0)
	                dp[i][j] = j;      
	            else if (j == 0)
	                dp[i][j] = i; 
	            else if (word1.charAt(i-1) == word2.charAt(j-1))
	                dp[i][j] = dp[i-1][j-1];	            
	            else if (i>1 && j>1  && word1.charAt(i-1) == word2.charAt(j-2) 
	            		&& word1.charAt(i-2) == word2.charAt(j-1))
	            	dp[i][j] = 1+Math.min(Math.min(dp[i-2][j-2], dp[i-1][j]), Math.min(dp[i][j-1], dp[i-1][j-1]));
	            else
	                dp[i][j] = 1 + Math.min(dp[i][j-1], Math.min(dp[i-1][j], dp[i-1][j-1])); 
	        }
	    } 
	    return dp[n][m];
	}
}
