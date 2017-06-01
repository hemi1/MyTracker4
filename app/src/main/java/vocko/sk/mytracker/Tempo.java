package vocko.sk.mytracker;


public class Tempo {
    private float lap;
    private float tempo;

    public Tempo(float lap, float tempo) {
        this.lap = lap;
        this.tempo = tempo;
    }

    public float getLap() {
        return lap;
    }

    public void setLap(float lap) {
        this.lap = lap;
    }

    public float getTempo() {
        return tempo;
    }

    public void setTempo(float tempo) {
        this.tempo = tempo;
    }
}
