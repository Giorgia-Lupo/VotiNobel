package it.polito.tdp.nobel.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.nobel.db.EsameDAO;

public class Model {

	private List<Esame> esami; //per riuscire a modellare l'ordine
	
	private double bestMedia = 0.0; //tiene traccia della media migliore incontrata
	private Set<Esame> bestSoluzione = null;//per dare in output un insieme ottimo di esami
	
	
	public Model () {
		EsameDAO dao = new EsameDAO();
		this.esami = dao.getTuttiEsami();
	}
	
	public Set<Esame> calcolaSottoinsiemeEsami(int numeroCrediti) {
		
		
		Set<Esame> parziale = new HashSet<>();
		
		cerca1(parziale, 0, numeroCrediti);
		
		return bestSoluzione; //se non trova nulla, ritorna null
	}
	
	
	/* APPROCCIO 1*/
	/* Complessità : 2^N */
	private void cerca1(Set<Esame> parziale, int L, int m) {
		
		//casi terminali
		
		int crediti = sommaCrediti(parziale);
		if(crediti > m) //non mi va bene
			return;
		
		if(crediti == m) {
			double media = calcolaMedia(parziale);//restituisce media dato un Set<Esame>
			if(media > bestMedia) { //bestMedia=medie incontrate fino ad adesso
				bestSoluzione = new HashSet<>(parziale);//sovrascrivo la soluzione ottima
				bestMedia = media;
			}
		}
		
		//sicuramente, crediti < m
		if(L == esami.size()) { //non ci sono più esami da considerare
			return ;
		}
		
		
		//generiamo i sotto-problemi
		//esami[L] è da aggiungere o no? Provo entrambe le cose
		
		//1. provo ad aggiungerlo
		parziale.add(esami.get(L)); //aggiungo a parziale l'l-esimo esame.
		cerca1(parziale, L+1,m); //ricorsione con il nuovo parziale
		parziale.remove(esami.get(L)); //backtrakking
		
		//2. provo a non aggiungerlo
		cerca1(parziale, L+1, m); //lancio parziale così com'è.
		
		
	}
	
	
	/* APPROCCIO 2 */
	/* Complessità : N! */
	private void cerca2(Set<Esame> parziale, int L, int m) {
		//casi terminali, uguali a cerca1
		
		int crediti = sommaCrediti(parziale);
		if(crediti > m)
			return;
				
		if(crediti == m) {
			double media = calcolaMedia(parziale);
			if(media > bestMedia) {
				bestSoluzione = new HashSet<>(parziale);
				bestMedia = media;
			}
		}
				
		//sicuramente, crediti < m
		if(L == esami.size()) {
			return ;
		}
		
		//generiamo i sotto-problemi
		for(Esame e : esami) { //a partire dal primo lo aggiungo a parziale, poi provo con il II°
			if(!parziale.contains(e)) { //controllo che non contenga già l'esame in questione
				parziale.add(e);
				cerca2(parziale, L + 1, m);
				parziale.remove(e); //al secondo giro del for, provo soluzioni che iniziano
				                    //con il secondo esame in parziale
			}
		}
	}

	public double calcolaMedia(Set<Esame> parziale) {
		int crediti = 0;
		int somma = 0;
		
		for(Esame e : parziale){
			crediti += e.getCrediti();
			somma += (e.getVoto() * e.getCrediti());
		}
		
		return somma/crediti;
	}

	private int sommaCrediti(Set<Esame> parziale) {
		int somma = 0;
		
		for(Esame e : parziale)
			somma += e.getCrediti();
		
		return somma;
	}

}
