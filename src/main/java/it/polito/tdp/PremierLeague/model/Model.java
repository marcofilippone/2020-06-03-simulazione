package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private Graph<Player, DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	private Map<Integer, Player> idMap;
	
	public Model() {
		dao = new PremierLeagueDAO();
	}

	public void creaGrafo(double media) {
		grafo = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
		idMap = new HashMap<>();
		dao.vertici(media, idMap);
		Graphs.addAllVertices(this.grafo, idMap.values());
		for(Arco arco : dao.edges(idMap)) {
			Graphs.addEdgeWithVertices(this.grafo, arco.getP1(), arco.getP2(), (int) arco.getPeso());
		}
	}
	
	public Set<Player> getVertexes(){
		return this.grafo.vertexSet();
	}
	
	public Set<DefaultWeightedEdge> getEdges(){
		return this.grafo.edgeSet();
	}
	
	public List<Arco> battuti(){
		List<Arco> lista = new ArrayList<>();
		Player best = null;
		int sconfitti = 0;
		for(Player p : this.getVertexes()) {
			if(sconfitti == 0 || grafo.outDegreeOf(p) > sconfitti) {
				best = p;
				sconfitti = grafo.outDegreeOf(p);
			}
		}
		if(best!=null) {
			for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(best)) {
				Arco a = new Arco(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), (int) grafo.getEdgeWeight(e));
				lista.add(a);
			}
		}
		Collections.sort(lista);
		return lista;
	}
}
