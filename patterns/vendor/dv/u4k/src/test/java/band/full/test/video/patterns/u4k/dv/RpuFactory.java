package band.full.test.video.patterns.u4k.dv;

import band.full.video.dolby.RPU;

@FunctionalInterface
public interface RpuFactory<A> {
    RPU create(A args, int fragment, int frame);
}
