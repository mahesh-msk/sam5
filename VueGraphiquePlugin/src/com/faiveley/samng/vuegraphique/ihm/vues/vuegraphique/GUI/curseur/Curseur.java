package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur;



public class Curseur {
	boolean curseurVisible;
	int positionCurseur;
	CursorPositionEvent ev;
	boolean addCursorAfterRedraw;
	int msgId;
	boolean SynchroniseCurseur;
    private static final Curseur INSTANCE = new Curseur();

    /**
     * La présence d'un constructeur privé supprime
     * le constructeur public par défaut.
     */
    private Curseur() {
    
    }
    
    
    /**
     * Dans ce cas présent, le mot-clé synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la méthode getInstance(). Donc aucun risque d'accès concurrents.
     * Retourne l'instance du singleton.
     */
    public static Curseur getInstance() {
        return INSTANCE;
    }
	
    public void setpositionCurseur(int pos){
    	this.positionCurseur=pos;
    }
    
    public int getpositionCurseur(){
    	return this.positionCurseur;
    }
    
    public void setCurseurVisible(boolean vis){
    	this.curseurVisible=vis;
    }
    
    public boolean getCurseurVisible(){
    	return this.curseurVisible;
    }


	public CursorPositionEvent getEv() {
		return ev;
	}


	public void setEv(CursorPositionEvent ev) {
		this.ev = ev;
	}

	public boolean isAddCursorAfterRedraw() {
		return addCursorAfterRedraw;
	}


	public void setAddCursorAfterRedraw(boolean addCursor) {
		this.addCursorAfterRedraw = addCursor;
	}

	public int getMsgId() {
		return msgId;
	}


	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}


	public boolean isSynchroniseCurseur() {
		return SynchroniseCurseur;
	}


	public void setSynchroniseCurseur(boolean synchroniseCurseur) {
		SynchroniseCurseur = synchroniseCurseur;
	}
}