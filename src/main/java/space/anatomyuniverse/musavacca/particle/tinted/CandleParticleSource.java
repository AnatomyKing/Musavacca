package space.anatomyuniverse.musavacca.particle.tinted;

public interface CandleParticleSource {
    OriginalCandleParticle musavacca$wrap();
    void musavacca$copyTo(OriginalCandleParticle target);
    float musavacca$u0();
    float musavacca$u1();
    float musavacca$v0();
    float musavacca$v1();
    int musavacca$light(float partialTick);
}
