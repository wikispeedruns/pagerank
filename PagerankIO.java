import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;


final class PagerankIO {

	private static final int PRINT_INTERVAL = 30;

	private PagerankIO() {}
	
	public static Map<Integer,String> readVertexMap(File file) throws IOException {
		long startTime = System.currentTimeMillis();
		Map<Integer,String> idToTitle = new HashMap<Integer,String>();
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			long lastPrint = System.currentTimeMillis() - PRINT_INTERVAL;
			String line;
    
		    while ((line = in.readLine()) != null) {
				int id = Integer.parseInt(line);
				String title = in.readLine();
				idToTitle.put(id, title);
				
				if (System.currentTimeMillis() - lastPrint >= PRINT_INTERVAL) {
					System.out.printf("\rReading %s: %.3f thousand entries...", file.getName(), idToTitle.size() / 1000.0);
					lastPrint = System.currentTimeMillis();
				}
			}
			System.out.printf("\rReading %s: %.3f thousand entries... Done (%.3f s)%n", file.getName(), idToTitle.size() / 1000.0, (System.currentTimeMillis() - startTime) / 1000.0);
		}
		return idToTitle;
	}

	public static Map<Integer, Set<Integer>> readEdgeMap(File file) throws IOException {
		long startTime = System.currentTimeMillis();
		Map<Integer, Set<Integer>> idFromIds = new HashMap<>();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			long lastPrint = System.currentTimeMillis() - PRINT_INTERVAL;
			String line;
    
		    while ((line = in.readLine()) != null) {
				int srcId = Integer.parseInt(line);
				int dstId = Integer.parseInt(in.readLine());

				idFromIds.putIfAbsent(dstId, new HashSet<>());
				idFromIds.get(dstId).add(srcId);

				idFromIds.putIfAbsent(srcId, new HashSet<>());

				if (System.currentTimeMillis() - lastPrint >= PRINT_INTERVAL) {
					System.out.printf("\rReading %s: %.3f thousand entries...", file.getName(), idFromIds.size() / 1000.0);
					lastPrint = System.currentTimeMillis();
				}
			}
			System.out.printf("\rReading %s: %.3f thousand entries... Done (%.3f s)%n", file.getName(), idFromIds.size() / 1000.0, (System.currentTimeMillis() - startTime) / 1000.0);
		}

		return idFromIds;
	}

	public static void writePageranks(File file, Map<Integer, String> idToTitle, Map<Integer, Double> pageranksById) throws IOException {
		List<Integer> ids = new ArrayList<>(pageranksById.keySet());
		Collections.sort(ids, (a,b) -> Double.compare(pageranksById.get(b), pageranksById.get(a)));
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			for (int id : ids)
				out.printf("%.3f\t%s\n", Math.log10(pageranksById.get(id)), idToTitle.get(id));
		}
	}
	
}