import java.util.*;

final class Pagerank {
	
	public Map<Integer, Double> pageranks;
	
	private Map<Integer, Set<Integer>> idFromIds;

	private Map<Integer, Integer> numOutgoingLinks;

	public Pagerank(Map<Integer, Set<Integer>> idFromIds) {
		this.idFromIds = idFromIds;
		
		this.numOutgoingLinks = new HashMap<>();
		for (Map.Entry<Integer, Set<Integer>> incomingLinks : idFromIds.entrySet()) {
			int dstId = incomingLinks.getKey();
			Set<Integer> srcIds = incomingLinks.getValue();
			this.numOutgoingLinks.putIfAbsent(dstId, 0);
			for (int srcId : srcIds) {
				this.numOutgoingLinks.put(srcId, this.numOutgoingLinks.getOrDefault(srcId, 0)+1);
			}
		}

		this.pageranks = new HashMap<>();
		for (int id : idFromIds.keySet()) {
			this.pageranks.put(id, 1.0 / idFromIds.size());
		}
	}
		
	public void iterateOnce(double damping) {
		Map<Integer, Double> newPageranks = new HashMap<>();

		for (Map.Entry<Integer, Set<Integer>> entry : this.idFromIds.entrySet()) {
			int dstId = entry.getKey();
			Set<Integer> srcIds = entry.getValue();
			double sum = 0;
			for (int srcId : srcIds) {
				sum += pageranks.get(srcId) / numOutgoingLinks.get(srcId); // should be safe div as numOutgoingLinks shouldn't be 0
			}
			newPageranks.put(dstId, sum);
		}
		
		double bias = 0;
		for (Map.Entry<Integer, Integer> entry : numOutgoingLinks.entrySet()) {
			int id = entry.getKey();
			int outgoingLinks = entry.getValue();
			if (outgoingLinks == 0)
				bias += pageranks.get(id);
		}
		bias /= idFromIds.size();

		double temp = bias * damping + (1 - damping) / idFromIds.size();
		for (Map.Entry<Integer, Double> entry : pageranks.entrySet()) {
			int id = entry.getKey();
			entry.setValue(newPageranks.get(id) * damping + temp);
		}
	}
	
}
