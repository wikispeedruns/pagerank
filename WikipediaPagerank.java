import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public final class WikipediaPagerank {
		
	private static final File PAGE_ID_TITLE_TXT_FILE = new File("wikipedia-pagerank-page-id-title.txt");
	private static final File PAGE_LINKS_TXT_FILE = new File("wikipedia-pagerank-page-links.txt");
	private static final File PAGERANKS_TXT_FILE = new File("wikipedia-pageranks.txt");

	private WikipediaPagerank() {}	
		
	public static void main(String[] args) throws IOException {
		Map<Integer,String> idToTitle = PagerankIO.readVertexMap(PAGE_ID_TITLE_TXT_FILE);
		Map<Integer, Set<Integer>> idFromIds = PagerankIO.readEdgeMap(PAGE_LINKS_TXT_FILE);
		
		final double DAMPING = 0.85;
		Pagerank pr = new Pagerank(idFromIds);
		Map<Integer, Double> prevPageranks = new HashMap<>(pr.pageranks);
		for (int i = 0; i < 1000; i++) {
			
			System.out.print("Iteration " + i);
			long startTime = System.currentTimeMillis();
			pr.iterateOnce(DAMPING);
			System.out.printf(" (%.3f s)%n", (System.currentTimeMillis() - startTime) / 1000.0);
			
			Map<Integer, Double> pageranks = pr.pageranks;
			printPagerankChangeRatios(prevPageranks, pageranks);
			printTopPages(pageranks, idToTitle);
			prevPageranks = new HashMap<>(pr.pageranks);
		}
		
		PagerankIO.writePageranks(PAGERANKS_TXT_FILE, idToTitle, pr.pageranks);
	}
		
	private static void printPagerankChangeRatios(Map<Integer, Double> prevPr, Map<Integer, Double> pr) {
		double min = Double.POSITIVE_INFINITY;
		double max = 0;

		for (Map.Entry<Integer, Double> entry : pr.entrySet()) {
			int id = entry.getKey();
			double score = entry.getValue();
			double prevScore = prevPr.get(id);
			if (score != 0 && prevScore != 0) {
				double ratio = score / prevScore;
				min = Math.min(ratio, min);
				max = Math.max(ratio, max);
			}
		}

		System.out.println("Range of ratio of changes: " + min + " to " + max);
	}
	
	
	private static void printTopPages(Map<Integer, Double> pageranksById, Map<Integer, String> titleById) {
		final int NUM_PAGES = 30;
		List<Integer> ids = new ArrayList<>(pageranksById.keySet());
		Collections.sort(ids, (a,b) -> Double.compare(pageranksById.get(b), pageranksById.get(a)));
		int count = 0;
		for (int id : ids) {
			if (count == NUM_PAGES) break;
			System.out.printf("  %.3f  %s%n", Math.log10(pageranksById.get(id)), titleById.get(id));
			count++;
		}
	}
	
}
