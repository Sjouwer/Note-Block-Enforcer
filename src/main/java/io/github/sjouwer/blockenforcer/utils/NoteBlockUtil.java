package io.github.sjouwer.blockenforcer.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Instrument;
import org.bukkit.block.data.type.NoteBlock;

public class NoteBlockUtil {
    private static final BiMap<String, Instrument> instrumentBiMap = HashBiMap.create();

    static {
        instrumentBiMap.put("HARP", Instrument.PIANO);
        instrumentBiMap.put("BASEDRUM", Instrument.BASS_DRUM);
        instrumentBiMap.put("SNARE", Instrument.SNARE_DRUM);
        instrumentBiMap.put("HAT", Instrument.STICKS);
        instrumentBiMap.put("BASS", Instrument.BASS_GUITAR);
        instrumentBiMap.put("FLUTE", Instrument.FLUTE);
        instrumentBiMap.put("BELL", Instrument.BELL);
        instrumentBiMap.put("GUITAR", Instrument.GUITAR);
        instrumentBiMap.put("CHIME", Instrument.CHIME);
        instrumentBiMap.put("XYLOPHONE", Instrument.XYLOPHONE);
        instrumentBiMap.put("IRON_XYLOPHONE", Instrument.IRON_XYLOPHONE);
        instrumentBiMap.put("COW_BELL", Instrument.COW_BELL);
        instrumentBiMap.put("DIDGERIDOO", Instrument.DIDGERIDOO);
        instrumentBiMap.put("BIT", Instrument.BIT);
        instrumentBiMap.put("BANJO", Instrument.BANJO);
        instrumentBiMap.put("PLING", Instrument.PLING);
    }

    private NoteBlockUtil() {
    }

    public static Instrument getInstrument(String nbtInstrument) {
        return instrumentBiMap.getOrDefault(nbtInstrument, Instrument.PIANO);
    }

    @SuppressWarnings("deprecation")
    public static String getName(NoteBlock block) {
        String powered = block.isPowered() ? "P-" : "";
        String instrument = instrumentBiMap.inverse().getOrDefault(block.getInstrument(), "HARP");
        String note = " " + block.getNote().getId();
        return powered + instrument + note;
    }
}
