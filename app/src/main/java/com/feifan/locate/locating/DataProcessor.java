package com.feifan.locate.locating;

import com.feifan.baselib.utils.LogUtils;
import com.feifan.locate.locating.data.AvlTree;
import com.feifan.scanlib.beacon.SampleBeacon;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 实时数据处理器
 * <p>
 *     目前支持：
 *     （1）beacon
 * </p>
 * Created by xuchunlei on 16/9/19.
 */
public class DataProcessor {

    private static final Map<String, Float> RESULT_CACHE = new HashMap<>();
    private static final Map<String, Integer> COUNT_CACHE = new HashMap<>();

    // filter
    private static final Map<String, String> FILTER_MAP_UUID = new HashMap<>();
    private static final Map<String, String> FILTER_MAP_MAJOR = new HashMap<>();
    private static final Map<String, AvlTree<Integer>> FILTER_MAP_MINOR = new HashMap<>();

    private static final String KEY_A22 = "A22";
    private static final String KEY_B31 = "B31";
    private static final String KEY_860100010060300001 = "android_860100010060300001";

    static {

        // A22
        FILTER_MAP_UUID.put(KEY_A22, "a3fce438-627c-42b7-ab72-dc6e55e137ac");
        FILTER_MAP_MAJOR.put(KEY_A22, "0,21112");
        int[] minor_a22 = new int[] {
                61249,61243,61242,61246,61288,61251,61250,61270,61320,64497
        };
        AvlTree<Integer> minors_a22 = new AvlTree<>();
        for(int value : minor_a22) {
            minors_a22.insert(value);
        }
        FILTER_MAP_MINOR.put(KEY_A22, minors_a22);

        // B31
        FILTER_MAP_UUID.put(KEY_B31, "ecb33b47-781f-4c16-8513-73fcbb7134f2");
        FILTER_MAP_MAJOR.put(KEY_B31, "100");
        int[] minor_b31 = new int[] {
                118,110,104,102,101,103,108,105,109,114,
                112,111,113,116,115,117,130,126,120,119,
                125,128,127,129,140,134,132,131,133,136,
                135,137,144,142,141,143,145,146

        };
        AvlTree<Integer> minors_b31 = new AvlTree<>();
        for(int value : minor_b31) {
            minors_b31.insert(value);
        }
        FILTER_MAP_MINOR.put(KEY_B31, minors_b31);

        // 860100010060300001
        FILTER_MAP_UUID.put(KEY_860100010060300001, "a3fce438-627c-42b7-ab72-dc6e55e137ac");
        FILTER_MAP_MAJOR.put(KEY_860100010060300001, "11000");
        int[] minor_860100010060300001 = new int[] {
                43102,42663,42553,42509,42496,42493,41633,41632,42492,42494,42495,42505,42503,42498,42497,42502,
                42504,42506,42507,42528,42515,42513,42512,42511,42514,42522,42517,42516,42519,42521,42526,42525,
                42523,42527,42539,42533,42531,42529,42532,42537,42534,42538,42547,42544,42541,42540,42542,42546,
                42545,42549,42548,42552,42551,42619,42580,42565,42561,42558,42555,42554,42557,42559,42560,42563,
                42562,42564,42572,42567,42566,42569,42568,42571,42576,42574,42575,42578,42577,42579,42596,42591,
                42589,42581,42590,42593,42592,42594,42595,42608,42598,42597,42606,42602,42607,42616,42614,42609,
                42615,42618,42617,42647,42633,42626,42623,42622,42621,42625,42629,42627,42628,42631,42630,42632,
                42640,42636,42634,42638,42637,42643,42641,42642,42645,42644,42646,42656,42650,42648,42649,42654,
                42652,42651,42653,42655,42658,42657,42661,42660,42662,42874,42834,42804,42681,42670,42668,42666,
                42665,42667,42669,42676,42673,42672,42675,42678,42679,42795,42791,42789,42788,42790,42793,42792,
                42794,42802,42800,42798,42801,42803,42819,42810,42807,42806,42805,42809,42814,42812,42811,42813,
                42816,42815,42818,42826,42823,42820,42822,42825,42824,42831,42829,42828,42830,42833,42849,42843,
                42837,42836,42835,42839,42841,42846,42844,42848,42860,42855,42852,42851,42850,42854,42853,42859,
                42857,42870,42865,42864,42862,42867,42866,42868,42872,42871,42873,43035,43002,42881,42877,42876,
                42875,42879,42878,42880,42991,42990,42988,42998,42997,42999,43001,43018,43009,43007,43005,43003,
                43008,43014,43011,43010,43013,43016,43015,43017,43026,43024,43021,43020,43022,43025,43030,43029,
                43027,43034,43033,43078,43064,43050,43044,43040,43038,43039,43042,43047,43045,43046,43049,43048,
                43057,43053,43052,43055,43054,43060,43058,43059,43062,43061,43073,43068,43066,43065,43071,43069,
                43072,43075,43074,43076,43077,43094,43088,43081,43079,43086,43084,43087,43092,43090,43089,43091,
                43093,43098,43096,43095,43097,43100,43099,43101,43262,43156,43121,43108,43105,43104,43103,43106,
                43107,43112,43110,43109,43111,43117,43115,43113,43116,43119,43142,43129,43126,43124,43123,43122,
                43125,43127,43128,43136,43131,43130,43134,43132,43135,43138,43137,43141,43149,43146,43143,43145,
                43148,43147,43152,43150,43154,43153,43155,43205,43182,43166,43161,43158,43157,43160,43164,43162,
                43163,43165,43176,43173,43171,43167,43172,43175,43174,43179,43177,43178,43181,43180,43194,43188,
                43185,43183,43184,43186,43187,43190,43189,43192,43193,43198,43196,43195,43201,43199,43203,43202,
                43204,43236,43225,43215,43209,43208,43206,43213,43210,43214,43221,43217,43216,43220,43223,43222,
                43224,43230,43228,43226,43227,43229,43233,43232,43231,43234,43245,43240,43237,43238,43241,43243,
                43251,43249,43248,43250,43255,43254,43253,43259,43258,43260,43411,43325,43294,43268,43264,43263,
                43265,43267,43287,43282,43270,43286,43284,43290,43288,43289,43292,43291,43293,43313,43307,43302,
                43297,43296,43301,43304,43303,43305,43311,43310,43309,43312,43318,43316,43315,43314,43317,43321,
                43320,43319,43322,43323,43357,43348,43333,43330,43328,43326,43329,43332,43341,43337,43335,43338,
                43343,43342,43352,43350,43349,43355,43354,43356,43392,43385,43361,43358,43359,43383,43382,43388,
                43386,43387,43390,43389,43391,43405,43399,43395,43394,43397,43396,43398,43401,43400,43403,43402,
                43404,43407,43406,43410,43409,43495,43449,43429,43417,43414,43412,43416,43415,43425,43419,43423,
                43427,43426,43428,43438,43436,43430,43437,43446,43443,43440,43444,43447,43486,43452,43450,43451,
                43482,43455,43483,43484,43490,43489,43492,43491,43493,43511,43506,43501,43498,43496,43500,43504,
                43503,43505,43508,43507,43510,43509,43517,43515,43513,43512,43514,43516,43519,43518,43520

        };
        AvlTree<Integer> minors_860100010060300001 = new AvlTree<>();
        for(int value : minor_860100010060300001) {
            minors_860100010060300001.insert(value);
        }
        FILTER_MAP_MINOR.put(KEY_860100010060300001, minors_860100010060300001);

    }

    private DataProcessor() {

    }

    public synchronized static Map<String, Float> processBeaconData(String position, Collection<SampleBeacon> data) {
        RESULT_CACHE.clear();
        COUNT_CACHE.clear();

        if(data != null && data.size() != 0) {
            String key;
            Float value;
            String filterUUID = FILTER_MAP_UUID.get(position);
            String filterMajor = FILTER_MAP_MAJOR.get(position);
            AvlTree<Integer> filterMinor = FILTER_MAP_MINOR.get(position);

            for(SampleBeacon sample : data) {
                if(!filterUUID.equalsIgnoreCase(sample.uuid)) {
                    continue;
                } else if(!filterMajor.contains(String.valueOf(sample.major))) {
                    continue;
                } else if(!filterMinor.contains(sample.minor)) {
                    continue;
                }
                key = sample.uuid + "_" + sample.major + "_" + sample.minor;
                value = RESULT_CACHE.get(key);
                if(value != null) {
                    RESULT_CACHE.put(key, value + sample.rssi);
                    COUNT_CACHE.put(key, COUNT_CACHE.get(key) + 1);
                }else {
                    RESULT_CACHE.put(key, (float)sample.rssi);
                    COUNT_CACHE.put(key, 1);
                }
            }

            Set<Entry<String, Integer>> cacheSet = COUNT_CACHE.entrySet();
            for(Entry<String, Integer> entry : cacheSet){
                value = RESULT_CACHE.get(entry.getKey());
                RESULT_CACHE.put(entry.getKey(), value / entry.getValue());
            }

        }

        return RESULT_CACHE;
    }
}
