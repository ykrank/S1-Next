package cl.monsoon.s1next.widget;

import android.content.Context;
import android.util.SparseArray;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cl.monsoon.s1next.R;

public final class EmoticonFactory {

    private final List<String> mEmoticonTypeTitles;
    private final SparseArray<Map<String, String>> mEmoticons;

    public EmoticonFactory(Context context) {
        mEmoticonTypeTitles = Collections.unmodifiableList(
                Arrays.asList(context.getResources().getStringArray(R.array.emoticon_type)));
        mEmoticons = new SparseArray<>();
    }

    public List<String> getTypeTitles() {
        return Collections.unmodifiableList(mEmoticonTypeTitles);
    }

    public Map<String, String> getByType(int type) {
        Map<String, String> emoticonTypeMap = mEmoticons.get(type);
        if (emoticonTypeMap == null) {
            switch (type) {
                case 0:
                    emoticonTypeMap = getFaceEmoticonMap();
                    break;
                case 1:
                    emoticonTypeMap = getDymEmoticonMap();
                    break;
                case 2:
                    emoticonTypeMap = getGooseEmoticonMap();
                    break;
                case 3:
                    emoticonTypeMap = getZdlEmoticonMap();
                    break;
                case 4:
                    emoticonTypeMap = getNqEmoticonMap();
                    break;
                case 5:
                    emoticonTypeMap = getNornamlEmoticonMap();
                    break;
                case 6:
                    emoticonTypeMap = getFlashEmoticonMap();
                    break;
                case 7:
                    emoticonTypeMap = getAnimalEmoticonMap();
                    break;
                case 8:
                    emoticonTypeMap = getCartonEmoticonMap();
                    break;
                case 9:
                    emoticonTypeMap = getBundamEmoticonMap();
                    break;
                default:
                    throw new IllegalArgumentException("Emoticon type can't be " + type + ".");
            }
        }
        mEmoticons.put(type, emoticonTypeMap);

        return new LinkedMap<>(emoticonTypeMap);
    }

    private Map<String, String> getFaceEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("face/91.gif", "[s:185]");
        map.put("face/64.gif", "{:3_562:}");
        map.put("face/63.gif", "{:3_563:}");
        map.put("face/67.gif", "{:3_564:}");
        map.put("face/69.gif", "{:3_565:}");
        map.put("face/71.gif", "{:3_566:}");
        map.put("face/54.gif", "[s:181]");
        map.put("face/163.gif", "[s:180]");
        map.put("face/61.gif", "[s:179]");
        map.put("face/160.gif", "[s:178]");
        map.put("face/89.gif", "[s:177]");
        map.put("face/101.gif", "[s:175]");
        map.put("face/56.gif", "[s:173]");
        map.put("face/116.gif", "[s:172]");
        map.put("face/78.gif", "{:3_561:}");
        map.put("face/76.gif", "{:3_560:}");
        map.put("face/72.gif", "{:3_559:}");
        map.put("face/174.gif", "[s:183]");
        map.put("face/176.gif", "[s:188]");
        map.put("face/95.gif", "[s:189]");
        map.put("face/60.gif", "[s:191]");
        map.put("face/93.gif", "[s:192]");
        map.put("face/79.gif", "[s:193]");
        map.put("face/134.gif", "[s:194]");
        map.put("face/169.jpg", "[s:182]");
        map.put("face/185.gif", "[s:197]");
        map.put("face/121.png", "[s:1238]");
        map.put("face/70.gif", "{:3_556:}");
        map.put("face/65.gif", "{:3_557:}");
        map.put("face/192.gif", "{:3_558:}");
        map.put("face/177.gif", "[s:171]");
        map.put("face/87.gif", "[s:170]");
        map.put("face/127.gif", "[s:158]");
        map.put("face/107.gif", "[s:196]");
        map.put("face/44.gif", "[s:156]");
        map.put("face/151.gif", "[s:155]");
        map.put("face/140.gif", "[s:154]");
        map.put("face/130.gif", "[s:153]");
        map.put("face/94.jpg", "[s:151]");
        map.put("face/119.gif", "[s:149]");
        map.put("face/153.gif", "[s:147]");
        map.put("face/73.gif", "{:3_567:}");
        map.put("face/62.gif", "{:3_568:}");
        map.put("face/68.gif", "{:3_569:}");
        map.put("face/83.gif", "{:3_570:}");
        map.put("face/191.gif", "[s:159]");
        map.put("face/86.gif", "[s:157]");
        map.put("face/124.jpg", "[s:186]");
        map.put("face/42.gif", "[s:168]");
        map.put("face/172.gif", "[s:165]");
        map.put("face/161.jpg", "[s:167]");
        map.put("face/41.gif", "[s:164]");
        map.put("face/50.gif", "[s:161]");
        map.put("face/00.gif", "[s:27]");
        map.put("face/149.gif", "[s:169]");
        map.put("face/136.gif", "[s:166]");
        map.put("face/01.gif", "[s:33]");
        map.put("face/02.gif", "[s:24]");
        map.put("face/03.gif", "[s:12]");
        map.put("face/04.gif", "[s:38]");
        map.put("face/05.gif", "[s:29]");
        map.put("face/06.gif", "[s:19]");
        map.put("face/07.gif", "[s:28]");
        map.put("face/08.gif", "[s:22]");
        map.put("face/09.gif", "[s:9]");
        map.put("face/10.gif", "[s:15]");
        map.put("face/11.gif", "[s:41]");
        map.put("face/12.gif", "[s:37]");
        map.put("face/13.gif", "[s:13]");
        map.put("face/14.gif", "[s:21]");
        map.put("face/15.gif", "[s:23]");
        map.put("face/16.gif", "[s:40]");
        map.put("face/17.gif", "[s:36]");
        map.put("face/18.gif", "[s:35]");
        map.put("face/19.gif", "[s:8]");
        map.put("face/20.gif", "[s:39]");
        map.put("face/21.gif", "[s:18]");
        map.put("face/22.gif", "[s:6]");
        map.put("face/23.gif", "[s:31]");
        map.put("face/24.gif", "[s:14]");
        map.put("face/25.gif", "[s:25]");
        map.put("face/26.gif", "[s:30]");
        map.put("face/27.gif", "[s:34]");
        map.put("face/28.gif", "[s:11]");
        map.put("face/29.gif", "[s:7]");
        map.put("face/30.gif", "[s:26]");
        map.put("face/31.gif", "[s:17]");
        map.put("face/32.gif", "[s:16]");
        map.put("face/33.gif", "[s:10]");
        map.put("face/34.gif", "[s:20]");
        map.put("face/35.gif", "[s:32]");
        map.put("face/150.gif", "[s:134]");
        map.put("face/129.gif", "[s:123]");
        map.put("face/179.gif", "[s:122]");
        map.put("face/37.gif", "[s:121]");
        map.put("face/43.gif", "[s:120]");
        map.put("face/183.gif", "[s:119]");
        map.put("face/181.gif", "[s:118]");
        map.put("face/52.gif", "[s:135]");
        map.put("face/106.gif", "[s:136]");
        map.put("face/170.gif", "[s:137]");
        map.put("face/111.gif", "[s:138]");
        map.put("face/39.gif", "[s:139]");
        map.put("face/156.jpg", "[s:140]");
        map.put("face/123.gif", "[s:141]");
        map.put("face/137.gif", "[s:143]");
        map.put("face/186.gif", "[s:144]");
        map.put("face/100.gif", "[s:46]");
        map.put("face/82.gif", "[s:116]");
        map.put("face/40.gif", "[s:115]");
        map.put("face/112.gif", "[s:44]");
        map.put("face/146.gif", "[s:72]");
        map.put("face/164.gif", "[s:76]");
        map.put("face/85.gif", "[s:77]");
        map.put("face/48.gif", "[s:79]");
        map.put("face/118.gif", "[s:80]");
        map.put("face/57.gif", "[s:81]");
        map.put("face/168.jpg", "[s:82]");
        map.put("face/190.gif", "[s:83]");
        map.put("face/74.gif", "[s:84]");
        map.put("face/77.gif", "[s:85]");
        map.put("face/141.gif", "[s:86]");
        map.put("face/45.gif", "[s:87]");
        map.put("face/182.jpg", "[s:88]");
        map.put("face/53.gif", "[s:89]");
        map.put("face/139.gif", "[s:90]");
        map.put("face/159.jpg", "[s:68]");
        map.put("face/178.gif", "[s:66]");
        map.put("face/145.gif", "[s:65]");
        map.put("face/47.gif", "[s:43]");
        map.put("face/154.gif", "[s:42]");
        map.put("face/152.gif", "[s:47]");
        map.put("face/97.gif", "[s:48]");
        map.put("face/96.gif", "[s:49]");
        map.put("face/173.gif", "[s:51]");
        map.put("face/188.gif", "[s:52]");
        map.put("face/99.gif", "[s:53]");
        map.put("face/135.gif", "[s:54]");
        map.put("face/49.gif", "[s:55]");
        map.put("face/148.gif", "[s:56]");
        map.put("face/105.gif", "[s:57]");
        map.put("face/143.gif", "[s:59]");
        map.put("face/108.gif", "[s:60]");
        map.put("face/38.gif", "[s:45]");
        map.put("face/147.gif", "[s:91]");
        map.put("face/120.gif", "[s:114]");
        map.put("face/92.gif", "[s:104]");
        map.put("face/55.gif", "[s:107]");
        map.put("face/167.gif", "[s:110]");
        map.put("face/46.gif", "[s:105]");
        map.put("face/157.gif", "[s:108]");
        map.put("face/180.gif", "[s:109]");
        map.put("face/131.gif", "[s:111]");
        map.put("face/133.gif", "[s:112]");
        map.put("face/75.gif", "[s:113]");
        map.put("face/162.jpg", "[s:102]");
        map.put("face/110.gif", "[s:101]");
        map.put("face/58.gif", "[s:103]");
        map.put("face/88.gif", "[s:98]");
        map.put("face/184.gif", "[s:99]");
        map.put("face/125.gif", "[s:97]");
        map.put("face/104.gif", "[s:95]");
        map.put("face/103.gif", "[s:94]");
        map.put("face/113.gif", "[s:93]");
        map.put("face/122.gif", "[s:92]");
        map.put("face/138.gif", "[s:100]");
        map.put("face/187.gif", "[s:58]");
        map.put("face/117.gif", "[s:128]");
        map.put("face/189.gif", "[s:127]");
        map.put("face/155.jpg", "[s:125]");
        map.put("face/158.jpg", "[s:117]");
        map.put("face/128.gif", "[s:124]");
        map.put("face/175.gif", "[s:96]");
        map.put("face/201.gif", "[s:50]");
        map.put("face/114.gif", "[s:67]");
        map.put("face/102.jpg", "[s:129]");
        map.put("face/80.gif", "[s:130]");
        map.put("face/98.gif", "[s:61]");
        map.put("face/59.gif", "[s:145]");
        map.put("face/132.gif", "[s:78]");
        map.put("face/142.gif", "[s:126]");
        map.put("face/81.gif", "[s:75]");
        map.put("face/51.gif", "[s:74]");
        map.put("face/171.gif", "[s:73]");
        map.put("face/166.gif", "[s:106]");
        map.put("face/84.gif", "[s:71]");
        map.put("face/115.gif", "[s:70]");
        map.put("face/165.gif", "[s:69]");
        map.put("face/109.gif", "[s:133]");
        map.put("face/90.gif", "[s:132]");
        map.put("face/126.gif", "[s:64]");
        map.put("face/200.gif", "[s:131]");
        map.put("face/66.gif", "[s:62]");
        map.put("face/144.gif", "[s:63]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getDymEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("dym/154.gif", "[s:1324]");
        map.put("dym/152.gif", "[s:1325]");
        map.put("dym/153.gif", "[s:1326]");
        map.put("dym/148.gif", "[s:1327]");
        map.put("dym/151.gif", "[s:1328]");
        map.put("dym/149.gif", "[s:1329]");
        map.put("dym/147.gif", "[s:1330]");
        map.put("dym/155.gif", "[s:1331]");
        map.put("dym/150.gif", "[s:1332]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getGooseEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("goose/186.gif", "[s:1539]");
        map.put("goose/180.gif", "[s:1519]");
        map.put("goose/157.gif", "[s:1518]");
        map.put("goose/302.gif", "[s:1517]");
        map.put("goose/166.gif", "[s:1516]");
        map.put("goose/09.gif", "[s:1515]");
        map.put("goose/46.gif", "[s:1514]");
        map.put("goose/160.gif", "[s:1513]");
        map.put("goose/b164.gif", "[s:1512]");
        map.put("goose/bdd.gif", "[s:1511]");
        map.put("goose/162.gif", "[s:1510]");
        map.put("goose/b185.gif", "[s:1509]");
        map.put("goose/92.gif", "[s:1508]");
        map.put("goose/992.gif", "[s:1507]");
        map.put("goose/d.gif", "[s:1506]");
        map.put("goose/58.gif", "[s:1505]");
        map.put("goose/30.gif", "[s:1504]");
        map.put("goose/184.gif", "[s:1503]");
        map.put("goose/6a.gif", "[s:1520]");
        map.put("goose/455.gif", "[s:1521]");
        map.put("goose/31.gif", "[s:1538]");
        map.put("goose/b200.gif", "[s:1537]");
        map.put("goose/19.gif", "[s:1536]");
        map.put("goose/e.gif", "[s:1535]");
        map.put("goose/159.gif", "[s:1534]");
        map.put("goose/b02.gif", "[s:1533]");
        map.put("goose/07.gif", "[s:1532]");
        map.put("goose/b11.gif", "[s:1531]");
        map.put("goose/170.gif", "[s:1530]");
        map.put("goose/12.gif", "[s:1529]");
        map.put("goose/06.gif", "[s:1528]");
        map.put("goose/5e.gif", "[s:1527]");
        map.put("goose/37.gif", "[s:1526]");
        map.put("goose/181.gif", "[s:1525]");
        map.put("goose/33.gif", "[s:1524]");
        map.put("goose/82.gif", "[s:1523]");
        map.put("goose/40.gif", "[s:1522]");
        map.put("goose/b112.gif", "[s:1502]");
        map.put("goose/88.gif", "[s:1501]");
        map.put("goose/178.gif", "[s:1481]");
        map.put("goose/01.gif", "[s:1480]");
        map.put("goose/28.gif", "[s:1479]");
        map.put("goose/50.gif", "[s:1478]");
        map.put("goose/161.gif", "[s:1477]");
        map.put("goose/0.7.gif", "[s:1476]");
        map.put("goose/187.gif", "[s:1475]");
        map.put("goose/08.gif", "[s:1474]");
        map.put("goose/49.gif", "[s:1473]");
        map.put("goose/10.gif", "[s:1472]");
        map.put("goose/34.gif", "[s:1471]");
        map.put("goose/13.gif", "[s:1470]");
        map.put("goose/97.gif", "[s:1469]");
        map.put("goose/152.gif", "[s:1468]");
        map.put("goose/100.gif", "[s:1467]");
        map.put("goose/38.gif", "[s:1466]");
        map.put("goose/112.gif", "[s:1465]");
        map.put("goose/114.gif", "[s:1482]");
        map.put("goose/456.gif", "[s:1483]");
        map.put("goose/11.gif", "[s:1500]");
        map.put("goose/29.gif", "[s:1499]");
        map.put("goose/53.gif", "[s:1498]");
        map.put("goose/45.gif", "[s:1497]");
        map.put("goose/3.gif", "[s:1496]");
        map.put("goose/158.gif", "[s:1495]");
        map.put("goose/b57.gif", "[s:1494]");
        map.put("goose/190.gif", "[s:1493]");
        map.put("goose/14.gif", "[s:1492]");
        map.put("goose/35.gif", "[s:1491]");
        map.put("goose/27.gif", "[s:1490]");
        map.put("goose/171.gif", "[s:1489]");
        map.put("goose/149.gif", "[s:1488]");
        map.put("goose/84.gif", "[s:1487]");
        map.put("goose/115.gif", "[s:1486]");
        map.put("goose/32.gif", "[s:1485]");
        map.put("goose/165.gif", "[s:1484]");
        map.put("goose/15.gif", "[s:1464]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getZdlEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("zdl/158.gif", "[s:1284]");
        map.put("zdl/161.gif", "[s:1283]");
        map.put("zdl/162.gif", "[s:1285]");
        map.put("zdl/156.gif", "[s:1286]");
        map.put("zdl/160.gif", "[s:1287]");
        map.put("zdl/157.gif", "[s:1288]");
        map.put("zdl/159.gif", "[s:1289]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getNqEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("nq/016.gif", "[s:1290]");
        map.put("nq/010.gif", "[s:1304]");
        map.put("nq/009.gif", "[s:1303]");
        map.put("nq/001.gif", "[s:1302]");
        map.put("nq/002.gif", "[s:1301]");
        map.put("nq/014.gif", "[s:1300]");
        map.put("nq/003.gif", "[s:1299]");
        map.put("nq/005.gif", "[s:1298]");
        map.put("nq/015.gif", "[s:1297]");
        map.put("nq/012.gif", "[s:1296]");
        map.put("nq/008.gif", "[s:1295]");
        map.put("nq/007.gif", "[s:1294]");
        map.put("nq/011.jpg", "[s:1293]");
        map.put("nq/004.gif", "[s:1292]");
        map.put("nq/006.gif", "[s:1291]");
        map.put("nq/013.gif", "[s:1305]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getNornamlEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("normal/058.gif", "[s:1409]");
        map.put("normal/026.gif", "[s:1423]");
        map.put("normal/110.gif", "[s:1424]");
        map.put("normal/077.gif", "[s:1425]");
        map.put("normal/101.gif", "[s:1426]");
        map.put("normal/052.gif", "[s:1427]");
        map.put("normal/108.jpg", "[s:1428]");
        map.put("normal/066.gif", "[s:1429]");
        map.put("normal/083.gif", "[s:1430]");
        map.put("normal/091.gif", "[s:1431]");
        map.put("normal/095.gif", "[s:1432]");
        map.put("normal/022.gif", "[s:1433]");
        map.put("normal/024.gif", "[s:1422]");
        map.put("normal/034.gif", "[s:1421]");
        map.put("normal/032.gif", "[s:1410]");
        map.put("normal/092.gif", "[s:1411]");
        map.put("normal/122.gif", "[s:1412]");
        map.put("normal/113.gif", "[s:1413]");
        map.put("normal/103.gif", "[s:1414]");
        map.put("normal/079.gif", "[s:1415]");
        map.put("normal/104.gif", "[s:1416]");
        map.put("normal/106.jpg", "[s:1417]");
        map.put("normal/082.gif", "[s:1418]");
        map.put("normal/102.gif", "[s:1419]");
        map.put("normal/039.gif", "[s:1420]");
        map.put("normal/045.jpg", "[s:1434]");
        map.put("normal/056.gif", "[s:1435]");
        map.put("normal/019.gif", "[s:1436]");
        map.put("normal/080.gif", "[s:1450]");
        map.put("normal/121.gif", "[s:1451]");
        map.put("normal/111.gif", "[s:1452]");
        map.put("normal/085.gif", "[s:1453]");
        map.put("normal/107.gif", "[s:1454]");
        map.put("normal/123.gif", "[s:1455]");
        map.put("normal/023.gif", "[s:1456]");
        map.put("normal/088.gif", "[s:1457]");
        map.put("normal/096.gif", "[s:1458]");
        map.put("normal/057.gif", "[s:1459]");
        map.put("normal/037.gif", "[s:1460]");
        map.put("normal/094.gif", "[s:1449]");
        map.put("normal/053.gif", "[s:1448]");
        map.put("normal/025.gif", "[s:1437]");
        map.put("normal/120.gif", "[s:1438]");
        map.put("normal/068.gif", "[s:1439]");
        map.put("normal/063.gif", "[s:1440]");
        map.put("normal/065.gif", "[s:1441]");
        map.put("normal/071.gif", "[s:1442]");
        map.put("normal/081.gif", "[s:1443]");
        map.put("normal/090.jpg", "[s:1444]");
        map.put("normal/117.gif", "[s:1445]");
        map.put("normal/049.jpg", "[s:1446]");
        map.put("normal/109.gif", "[s:1447]");
        map.put("normal/062.gif", "[s:1461]");
        map.put("normal/076.gif", "[s:1408]");
        map.put("normal/017.gif", "[s:1355]");
        map.put("normal/051.gif", "[s:1369]");
        map.put("normal/064.gif", "[s:1370]");
        map.put("normal/020.gif", "[s:1371]");
        map.put("normal/041.gif", "[s:1372]");
        map.put("normal/054.gif", "[s:1373]");
        map.put("normal/072.gif", "[s:1374]");
        map.put("normal/119.gif", "[s:1375]");
        map.put("normal/098.gif", "[s:1376]");
        map.put("normal/089.gif", "[s:1377]");
        map.put("normal/044.gif", "[s:1378]");
        map.put("normal/105.gif", "[s:1379]");
        map.put("normal/038.gif", "[s:1368]");
        map.put("normal/073.gif", "[s:1367]");
        map.put("normal/021.gif", "[s:1356]");
        map.put("normal/087.gif", "[s:1357]");
        map.put("normal/074.gif", "[s:1358]");
        map.put("normal/112.gif", "[s:1359]");
        map.put("normal/086.gif", "[s:1360]");
        map.put("normal/093.gif", "[s:1361]");
        map.put("normal/100.gif", "[s:1362]");
        map.put("normal/047.gif", "[s:1363]");
        map.put("normal/043.gif", "[s:1364]");
        map.put("normal/050.gif", "[s:1365]");
        map.put("normal/035.gif", "[s:1366]");
        map.put("normal/075.gif", "[s:1380]");
        map.put("normal/031.gif", "[s:1381]");
        map.put("normal/084.gif", "[s:1382]");
        map.put("normal/061.gif", "[s:1396]");
        map.put("normal/033.gif", "[s:1397]");
        map.put("normal/067.gif", "[s:1398]");
        map.put("normal/048.jpg", "[s:1399]");
        map.put("normal/059.gif", "[s:1400]");
        map.put("normal/029.gif", "[s:1401]");
        map.put("normal/116.gif", "[s:1402]");
        map.put("normal/060.gif", "[s:1403]");
        map.put("normal/115.jpg", "[s:1404]");
        map.put("normal/097.gif", "[s:1405]");
        map.put("normal/042.png", "[s:1406]");
        map.put("normal/036.gif", "[s:1395]");
        map.put("normal/027.gif", "[s:1394]");
        map.put("normal/099.gif", "[s:1383]");
        map.put("normal/046.gif", "[s:1384]");
        map.put("normal/018.gif", "[s:1385]");
        map.put("normal/078.gif", "[s:1386]");
        map.put("normal/114.gif", "[s:1387]");
        map.put("normal/030.gif", "[s:1388]");
        map.put("normal/070.gif", "[s:1389]");
        map.put("normal/028.gif", "[s:1390]");
        map.put("normal/040.gif", "[s:1391]");
        map.put("normal/124.gif", "[s:1392]");
        map.put("normal/118.gif", "[s:1393]");
        map.put("normal/055.gif", "[s:1407]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getFlashEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("flash/135.gif", "[s:1343]");
        map.put("flash/128.gif", "[s:1353]");
        map.put("flash/129.gif", "[s:1352]");
        map.put("flash/133.gif", "[s:1351]");
        map.put("flash/131.gif", "[s:1350]");
        map.put("flash/125.gif", "[s:1349]");
        map.put("flash/132.gif", "[s:1348]");
        map.put("flash/136.gif", "[s:1347]");
        map.put("flash/126.gif", "[s:1346]");
        map.put("flash/127.gif", "[s:1345]");
        map.put("flash/130.gif", "[s:1344]");
        map.put("flash/134.gif", "[s:1354]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getAnimalEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("animal/140.gif", "[s:1333]");
        map.put("animal/137.gif", "[s:1342]");
        map.put("animal/142.gif", "[s:1341]");
        map.put("animal/145.jpg", "[s:1340]");
        map.put("animal/138.gif", "[s:1339]");
        map.put("animal/139.gif", "[s:1338]");
        map.put("animal/141.gif", "[s:1337]");
        map.put("animal/146.gif", "[s:1336]");
        map.put("animal/144.gif", "[s:1335]");
        map.put("animal/143.gif", "[s:1334]");
        map.put("animal/203.gif", "[s:1462]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getCartonEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("carton/173.gif", "[s:1306]");
        map.put("carton/169.gif", "[s:1322]");
        map.put("carton/176.gif", "[s:1321]");
        map.put("carton/172.jpg", "[s:1320]");
        map.put("carton/179.gif", "[s:1319]");
        map.put("carton/174.gif", "[s:1318]");
        map.put("carton/168.gif", "[s:1317]");
        map.put("carton/167.gif", "[s:1316]");
        map.put("carton/178.jpg", "[s:1315]");
        map.put("carton/180.gif", "[s:1314]");
        map.put("carton/166.gif", "[s:1313]");
        map.put("carton/175.gif", "[s:1312]");
        map.put("carton/177.gif", "[s:1311]");
        map.put("carton/164.gif", "[s:1310]");
        map.put("carton/171.gif", "[s:1309]");
        map.put("carton/165.gif", "[s:1308]");
        map.put("carton/163.jpg", "[s:1307]");
        map.put("carton/170.gif", "[s:1323]");
        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getBundamEmoticonMap() {
        Map<String, String> map = new LinkedMap<>();
        map.put("bundam/7.png", "[s:1240]");
        map.put("bundam/17.png", "[s:1259]");
        map.put("bundam/16.png", "[s:1260]");
        map.put("bundam/11.png", "[s:1261]");
        map.put("bundam/21.png", "[s:1262]");
        map.put("bundam/62.gif", "[s:1264]");
        map.put("bundam/78.gif", "[s:1265]");
        map.put("bundam/69.gif", "[s:1266]");
        map.put("bundam/73.gif", "[s:1267]");
        map.put("bundam/68.gif", "[s:1268]");
        map.put("bundam/71.gif", "[s:1269]");
        map.put("bundam/72.gif", "[s:1270]");
        map.put("bundam/63.gif", "[s:1271]");
        map.put("bundam/83.gif", "[s:1272]");
        map.put("bundam/70.gif", "[s:1273]");
        map.put("bundam/76.gif", "[s:1274]");
        map.put("bundam/22.png", "[s:1258]");
        map.put("bundam/10.png", "[s:1257]");
        map.put("bundam/3.png", "[s:1256]");
        map.put("bundam/65.gif", "[s:1263]");
        map.put("bundam/18.png", "[s:1242]");
        map.put("bundam/13.png", "[s:1243]");
        map.put("bundam/15.png", "[s:1244]");
        map.put("bundam/9.png", "[s:1245]");
        map.put("bundam/2.png", "[s:1246]");
        map.put("bundam/5.png", "[s:1247]");
        map.put("bundam/4.png", "[s:1248]");
        map.put("bundam/14.png", "[s:1249]");
        map.put("bundam/20.png", "[s:1250]");
        map.put("bundam/6.png", "[s:1251]");
        map.put("bundam/23.png", "[s:1252]");
        map.put("bundam/8.png", "[s:1253]");
        map.put("bundam/19.png", "[s:1254]");
        map.put("bundam/12.png", "[s:1255]");
        map.put("bundam/64.gif", "[s:1275]");
        return Collections.unmodifiableMap(map);
    }
}
