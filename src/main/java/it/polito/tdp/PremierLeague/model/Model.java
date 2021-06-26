package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
	private List<Player> soluzioneMigliore;
	private int k;
	private int sommaGrado;
	private int sommaGradoBest;
	
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
	
	public List<Player> dreamTeam(int k){
		List<Player> parziale = new LinkedList<>();
		this.soluzioneMigliore = new LinkedList<>();
		this.k=k;
		this.sommaGrado = 0;
		this.sommaGradoBest = 0;
		List<Player> battuti = new LinkedList<>();
		
		cerca(parziale, battuti);
		
		return this.soluzioneMigliore;
	}
	
	private void cerca(List<Player> parziale, List<Player> battuti) {
		if(parziale.size() == this.k) {
			if(this.sommaGradoBest == 0 || this.sommaGrado>this.sommaGradoBest) {
				this.soluzioneMigliore = new LinkedList<>(parziale);
				this.sommaGradoBest = this.sommaGrado;
			}
			return;
		}
		
		for(Player p : this.grafo.vertexSet()) {
			if(!parziale.contains(p) && !battuti.contains(p)) {
				int grado = this.calcolaOut(p) - this.calcolaIn(p);
				parziale.add(p);
				List<Player> nuoviBattuti = this.trovaBattuti(p, battuti);
				battuti.addAll(nuoviBattuti);
				this.sommaGrado += grado;
				
				cerca(parziale, battuti);
				
				this.sommaGrado -= grado;
				battuti.removeAll(nuoviBattuti);
				parziale.remove(parziale.size()-1);
			}
		}
	}
	
	public int calcolaOut(Player player) {
		int out = 0;
		for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(player)) {
			out += grafo.getEdgeWeight(e);
		}
		return out;
	}
	
	public int calcolaIn(Player player) {
		int in = 0;
		for(DefaultWeightedEdge e : grafo.incomingEdgesOf(player)) {
			in += grafo.getEdgeWeight(e);
		}
		return in;
	}
	
	private List<Player> trovaBattuti(Player p, List<Player> battuti) { //cerco i battuti nuovi, cosi quando faccio backtracking e li devo rimuovere non tolgo dei battuti che erano gia presenti nella lista
		List<Player> listaNuovi = new ArrayList<>();
		for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(p)) {
			if(!battuti.contains(grafo.getEdgeTarget(e))) {
				listaNuovi.add(grafo.getEdgeTarget(e));
			}
		}
		return listaNuovi;
	}
	
	public int getGradoTot() {
		return this.sommaGradoBest;
	}
}
