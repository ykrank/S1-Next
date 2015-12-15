package cl.monsoon.s1next.widget;

import android.content.Context;
import android.util.SparseArray;

import com.google.common.collect.ImmutableList;

import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Emoticon;

/**
 * A factory provides emotions.
 */
public final class EmoticonFactory {

    public static final String ASSET_PATH_EMOTICON = "file:///android_asset/image/emoticon/";

    private final List<String> mEmoticonTypeTitles;
    private final SparseArray<List<Emoticon>> mEmoticons;

    public EmoticonFactory(Context context) {
        mEmoticonTypeTitles = ImmutableList.copyOf(context.getResources().getStringArray(
                R.array.emoticon_type));
        mEmoticons = new SparseArray<>();
    }

    public List<String> getEmotionTypeTitles() {
        return mEmoticonTypeTitles;
    }

    public List<Emoticon> getEmoticonsByIndex(int index) {
        List<Emoticon> emoticons = mEmoticons.get(index);
        if (emoticons == null) {
            switch (index) {
                case 0:
                    emoticons = getFaceEmoticonList();
                    break;
                case 1:
                    emoticons = getDymEmoticonList();
                    break;
                case 2:
                    emoticons = getGooseEmoticonList();
                    break;
                case 3:
                    emoticons = getZdlEmoticonList();
                    break;
                case 4:
                    emoticons = getNqEmoticonList();
                    break;
                case 5:
                    emoticons = getNormalEmoticonList();
                    break;
                case 6:
                    emoticons = getFlashEmoticonList();
                    break;
                case 7:
                    emoticons = getAnimalEmoticonList();
                    break;
                case 8:
                    emoticons = getCartonEmoticonList();
                    break;
                case 9:
                    emoticons = getBundamEmoticonList();
                    break;
                default:
                    throw new IllegalStateException("Unknown emoticon index: " + index + ".");
            }
        }
        mEmoticons.put(index, emoticons);

        return emoticons;
    }

    private List<Emoticon> getFaceEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "face/91.gif", "[s:185]");
        add(builder, "face/64.gif", "{:3_562:}");
        add(builder, "face/63.gif", "{:3_563:}");
        add(builder, "face/67.gif", "{:3_564:}");
        add(builder, "face/69.gif", "{:3_565:}");
        add(builder, "face/71.gif", "{:3_566:}");
        add(builder, "face/54.gif", "[s:181]");
        add(builder, "face/163.gif", "[s:180]");
        add(builder, "face/61.gif", "[s:179]");
        add(builder, "face/160.gif", "[s:178]");
        add(builder, "face/89.gif", "[s:177]");
        add(builder, "face/101.gif", "[s:175]");
        add(builder, "face/56.gif", "[s:173]");
        add(builder, "face/116.gif", "[s:172]");
        add(builder, "face/78.gif", "{:3_561:}");
        add(builder, "face/76.gif", "{:3_560:}");
        add(builder, "face/72.gif", "{:3_559:}");
        add(builder, "face/174.gif", "[s:183]");
        add(builder, "face/176.gif", "[s:188]");
        add(builder, "face/95.gif", "[s:189]");
        add(builder, "face/60.gif", "[s:191]");
        add(builder, "face/93.gif", "[s:192]");
        add(builder, "face/79.gif", "[s:193]");
        add(builder, "face/134.gif", "[s:194]");
        add(builder, "face/169.jpg", "[s:182]");
        add(builder, "face/185.gif", "[s:197]");
        add(builder, "face/121.png", "[s:1238]");
        add(builder, "face/70.gif", "{:3_556:}");
        add(builder, "face/65.gif", "{:3_557:}");
        add(builder, "face/192.gif", "{:3_558:}");
        add(builder, "face/177.gif", "[s:171]");
        add(builder, "face/87.gif", "[s:170]");
        add(builder, "face/127.gif", "[s:158]");
        add(builder, "face/107.gif", "[s:196]");
        add(builder, "face/44.gif", "[s:156]");
        add(builder, "face/151.gif", "[s:155]");
        add(builder, "face/140.gif", "[s:154]");
        add(builder, "face/130.gif", "[s:153]");
        add(builder, "face/94.jpg", "[s:151]");
        add(builder, "face/119.gif", "[s:149]");
        add(builder, "face/153.gif", "[s:147]");
        add(builder, "face/73.gif", "{:3_567:}");
        add(builder, "face/62.gif", "{:3_568:}");
        add(builder, "face/68.gif", "{:3_569:}");
        add(builder, "face/83.gif", "{:3_570:}");
        add(builder, "face/191.gif", "[s:159]");
        add(builder, "face/86.gif", "[s:157]");
        add(builder, "face/124.jpg", "[s:186]");
        add(builder, "face/42.gif", "[s:168]");
        add(builder, "face/172.gif", "[s:165]");
        add(builder, "face/161.jpg", "[s:167]");
        add(builder, "face/41.gif", "[s:164]");
        add(builder, "face/50.gif", "[s:161]");
        add(builder, "face/00.gif", "[s:27]");
        add(builder, "face/149.gif", "[s:169]");
        add(builder, "face/136.gif", "[s:166]");
        add(builder, "face/01.gif", "[s:33]");
        add(builder, "face/02.gif", "[s:24]");
        add(builder, "face/03.gif", "[s:12]");
        add(builder, "face/04.gif", "[s:38]");
        add(builder, "face/05.gif", "[s:29]");
        add(builder, "face/06.gif", "[s:19]");
        add(builder, "face/07.gif", "[s:28]");
        add(builder, "face/08.gif", "[s:22]");
        add(builder, "face/09.gif", "[s:9]");
        add(builder, "face/10.gif", "[s:15]");
        add(builder, "face/11.gif", "[s:41]");
        add(builder, "face/12.gif", "[s:37]");
        add(builder, "face/13.gif", "[s:13]");
        add(builder, "face/14.gif", "[s:21]");
        add(builder, "face/15.gif", "[s:23]");
        add(builder, "face/16.gif", "[s:40]");
        add(builder, "face/17.gif", "[s:36]");
        add(builder, "face/18.gif", "[s:35]");
        add(builder, "face/19.gif", "[s:8]");
        add(builder, "face/20.gif", "[s:39]");
        add(builder, "face/21.gif", "[s:18]");
        add(builder, "face/22.gif", "[s:6]");
        add(builder, "face/23.gif", "[s:31]");
        add(builder, "face/24.gif", "[s:14]");
        add(builder, "face/25.gif", "[s:25]");
        add(builder, "face/26.gif", "[s:30]");
        add(builder, "face/27.gif", "[s:34]");
        add(builder, "face/28.gif", "[s:11]");
        add(builder, "face/29.gif", "[s:7]");
        add(builder, "face/30.gif", "[s:26]");
        add(builder, "face/31.gif", "[s:17]");
        add(builder, "face/32.gif", "[s:16]");
        add(builder, "face/33.gif", "[s:10]");
        add(builder, "face/34.gif", "[s:20]");
        add(builder, "face/35.gif", "[s:32]");
        add(builder, "face/150.gif", "[s:134]");
        add(builder, "face/129.gif", "[s:123]");
        add(builder, "face/179.gif", "[s:122]");
        add(builder, "face/37.gif", "[s:121]");
        add(builder, "face/43.gif", "[s:120]");
        add(builder, "face/183.gif", "[s:119]");
        add(builder, "face/181.gif", "[s:118]");
        add(builder, "face/52.gif", "[s:135]");
        add(builder, "face/106.gif", "[s:136]");
        add(builder, "face/170.gif", "[s:137]");
        add(builder, "face/111.gif", "[s:138]");
        add(builder, "face/39.gif", "[s:139]");
        add(builder, "face/156.jpg", "[s:140]");
        add(builder, "face/123.gif", "[s:141]");
        add(builder, "face/137.gif", "[s:143]");
        add(builder, "face/186.gif", "[s:144]");
        add(builder, "face/100.gif", "[s:46]");
        add(builder, "face/82.gif", "[s:116]");
        add(builder, "face/40.gif", "[s:115]");
        add(builder, "face/112.gif", "[s:44]");
        add(builder, "face/146.gif", "[s:72]");
        add(builder, "face/164.gif", "[s:76]");
        add(builder, "face/85.gif", "[s:77]");
        add(builder, "face/48.gif", "[s:79]");
        add(builder, "face/118.gif", "[s:80]");
        add(builder, "face/57.gif", "[s:81]");
        add(builder, "face/168.jpg", "[s:82]");
        add(builder, "face/190.gif", "[s:83]");
        add(builder, "face/74.gif", "[s:84]");
        add(builder, "face/77.gif", "[s:85]");
        add(builder, "face/141.gif", "[s:86]");
        add(builder, "face/45.gif", "[s:87]");
        add(builder, "face/182.jpg", "[s:88]");
        add(builder, "face/53.gif", "[s:89]");
        add(builder, "face/139.gif", "[s:90]");
        add(builder, "face/159.jpg", "[s:68]");
        add(builder, "face/178.gif", "[s:66]");
        add(builder, "face/145.gif", "[s:65]");
        add(builder, "face/47.gif", "[s:43]");
        add(builder, "face/154.gif", "[s:42]");
        add(builder, "face/152.gif", "[s:47]");
        add(builder, "face/97.gif", "[s:48]");
        add(builder, "face/96.gif", "[s:49]");
        add(builder, "face/173.gif", "[s:51]");
        add(builder, "face/188.gif", "[s:52]");
        add(builder, "face/99.gif", "[s:53]");
        add(builder, "face/135.gif", "[s:54]");
        add(builder, "face/49.gif", "[s:55]");
        add(builder, "face/148.gif", "[s:56]");
        add(builder, "face/105.gif", "[s:57]");
        add(builder, "face/143.gif", "[s:59]");
        add(builder, "face/108.gif", "[s:60]");
        add(builder, "face/38.gif", "[s:45]");
        add(builder, "face/147.gif", "[s:91]");
        add(builder, "face/120.gif", "[s:114]");
        add(builder, "face/92.gif", "[s:104]");
        add(builder, "face/55.gif", "[s:107]");
        add(builder, "face/167.gif", "[s:110]");
        add(builder, "face/46.gif", "[s:105]");
        add(builder, "face/157.gif", "[s:108]");
        add(builder, "face/180.gif", "[s:109]");
        add(builder, "face/131.gif", "[s:111]");
        add(builder, "face/133.gif", "[s:112]");
        add(builder, "face/75.gif", "[s:113]");
        add(builder, "face/162.jpg", "[s:102]");
        add(builder, "face/110.gif", "[s:101]");
        add(builder, "face/58.gif", "[s:103]");
        add(builder, "face/88.gif", "[s:98]");
        add(builder, "face/184.gif", "[s:99]");
        add(builder, "face/125.gif", "[s:97]");
        add(builder, "face/104.gif", "[s:95]");
        add(builder, "face/103.gif", "[s:94]");
        add(builder, "face/113.gif", "[s:93]");
        add(builder, "face/122.gif", "[s:92]");
        add(builder, "face/138.gif", "[s:100]");
        add(builder, "face/187.gif", "[s:58]");
        add(builder, "face/117.gif", "[s:128]");
        add(builder, "face/189.gif", "[s:127]");
        add(builder, "face/155.jpg", "[s:125]");
        add(builder, "face/158.jpg", "[s:117]");
        add(builder, "face/128.gif", "[s:124]");
        add(builder, "face/175.gif", "[s:96]");
        add(builder, "face/201.gif", "[s:50]");
        add(builder, "face/114.gif", "[s:67]");
        add(builder, "face/102.jpg", "[s:129]");
        add(builder, "face/80.gif", "[s:130]");
        add(builder, "face/98.gif", "[s:61]");
        add(builder, "face/59.gif", "[s:145]");
        add(builder, "face/132.gif", "[s:78]");
        add(builder, "face/142.gif", "[s:126]");
        add(builder, "face/81.gif", "[s:75]");
        add(builder, "face/51.gif", "[s:74]");
        add(builder, "face/171.gif", "[s:73]");
        add(builder, "face/166.gif", "[s:106]");
        add(builder, "face/84.gif", "[s:71]");
        add(builder, "face/115.gif", "[s:70]");
        add(builder, "face/165.gif", "[s:69]");
        add(builder, "face/109.gif", "[s:133]");
        add(builder, "face/90.gif", "[s:132]");
        add(builder, "face/126.gif", "[s:64]");
        add(builder, "face/200.gif", "[s:131]");
        add(builder, "face/66.gif", "[s:62]");
        add(builder, "face/144.gif", "[s:63]");
        return builder.build();
    }

    private List<Emoticon> getDymEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "dym/154.gif", "[s:1324]");
        add(builder, "dym/152.gif", "[s:1325]");
        add(builder, "dym/153.gif", "[s:1326]");
        add(builder, "dym/148.gif", "[s:1327]");
        add(builder, "dym/151.gif", "[s:1328]");
        add(builder, "dym/149.gif", "[s:1329]");
        add(builder, "dym/147.gif", "[s:1330]");
        add(builder, "dym/155.gif", "[s:1331]");
        add(builder, "dym/150.gif", "[s:1332]");
        return builder.build();
    }

    private List<Emoticon> getGooseEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "goose/186.gif", "[s:1539]");
        add(builder, "goose/180.gif", "[s:1519]");
        add(builder, "goose/157.gif", "[s:1518]");
        add(builder, "goose/302.gif", "[s:1517]");
        add(builder, "goose/166.gif", "[s:1516]");
        add(builder, "goose/09.gif", "[s:1515]");
        add(builder, "goose/46.gif", "[s:1514]");
        add(builder, "goose/160.gif", "[s:1513]");
        add(builder, "goose/b164.gif", "[s:1512]");
        add(builder, "goose/bdd.gif", "[s:1511]");
        add(builder, "goose/162.gif", "[s:1510]");
        add(builder, "goose/b185.gif", "[s:1509]");
        add(builder, "goose/92.gif", "[s:1508]");
        add(builder, "goose/992.gif", "[s:1507]");
        add(builder, "goose/d.gif", "[s:1506]");
        add(builder, "goose/58.gif", "[s:1505]");
        add(builder, "goose/30.gif", "[s:1504]");
        add(builder, "goose/184.gif", "[s:1503]");
        add(builder, "goose/6a.gif", "[s:1520]");
        add(builder, "goose/455.gif", "[s:1521]");
        add(builder, "goose/31.gif", "[s:1538]");
        add(builder, "goose/b200.gif", "[s:1537]");
        add(builder, "goose/19.gif", "[s:1536]");
        add(builder, "goose/e.gif", "[s:1535]");
        add(builder, "goose/159.gif", "[s:1534]");
        add(builder, "goose/b02.gif", "[s:1533]");
        add(builder, "goose/07.gif", "[s:1532]");
        add(builder, "goose/b11.gif", "[s:1531]");
        add(builder, "goose/170.gif", "[s:1530]");
        add(builder, "goose/12.gif", "[s:1529]");
        add(builder, "goose/06.gif", "[s:1528]");
        add(builder, "goose/5e.gif", "[s:1527]");
        add(builder, "goose/37.gif", "[s:1526]");
        add(builder, "goose/181.gif", "[s:1525]");
        add(builder, "goose/33.gif", "[s:1524]");
        add(builder, "goose/82.gif", "[s:1523]");
        add(builder, "goose/40.gif", "[s:1522]");
        add(builder, "goose/b112.gif", "[s:1502]");
        add(builder, "goose/88.gif", "[s:1501]");
        add(builder, "goose/178.gif", "[s:1481]");
        add(builder, "goose/01.gif", "[s:1480]");
        add(builder, "goose/28.gif", "[s:1479]");
        add(builder, "goose/50.gif", "[s:1478]");
        add(builder, "goose/161.gif", "[s:1477]");
        add(builder, "goose/0.7.gif", "[s:1476]");
        add(builder, "goose/187.gif", "[s:1475]");
        add(builder, "goose/08.gif", "[s:1474]");
        add(builder, "goose/49.gif", "[s:1473]");
        add(builder, "goose/10.gif", "[s:1472]");
        add(builder, "goose/34.gif", "[s:1471]");
        add(builder, "goose/13.gif", "[s:1470]");
        add(builder, "goose/97.gif", "[s:1469]");
        add(builder, "goose/152.gif", "[s:1468]");
        add(builder, "goose/100.gif", "[s:1467]");
        add(builder, "goose/38.gif", "[s:1466]");
        add(builder, "goose/112.gif", "[s:1465]");
        add(builder, "goose/114.gif", "[s:1482]");
        add(builder, "goose/456.gif", "[s:1483]");
        add(builder, "goose/11.gif", "[s:1500]");
        add(builder, "goose/29.gif", "[s:1499]");
        add(builder, "goose/53.gif", "[s:1498]");
        add(builder, "goose/45.gif", "[s:1497]");
        add(builder, "goose/3.gif", "[s:1496]");
        add(builder, "goose/158.gif", "[s:1495]");
        add(builder, "goose/b57.gif", "[s:1494]");
        add(builder, "goose/190.gif", "[s:1493]");
        add(builder, "goose/14.gif", "[s:1492]");
        add(builder, "goose/35.gif", "[s:1491]");
        add(builder, "goose/27.gif", "[s:1490]");
        add(builder, "goose/171.gif", "[s:1489]");
        add(builder, "goose/149.gif", "[s:1488]");
        add(builder, "goose/84.gif", "[s:1487]");
        add(builder, "goose/115.gif", "[s:1486]");
        add(builder, "goose/32.gif", "[s:1485]");
        add(builder, "goose/165.gif", "[s:1484]");
        add(builder, "goose/15.gif", "[s:1464]");
        return builder.build();
    }

    private List<Emoticon> getZdlEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "zdl/158.gif", "[s:1284]");
        add(builder, "zdl/161.gif", "[s:1283]");
        add(builder, "zdl/162.gif", "[s:1285]");
        add(builder, "zdl/156.gif", "[s:1286]");
        add(builder, "zdl/160.gif", "[s:1287]");
        add(builder, "zdl/157.gif", "[s:1288]");
        add(builder, "zdl/159.gif", "[s:1289]");
        return builder.build();
    }

    private List<Emoticon> getNqEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "nq/016.gif", "[s:1290]");
        add(builder, "nq/010.gif", "[s:1304]");
        add(builder, "nq/009.gif", "[s:1303]");
        add(builder, "nq/001.gif", "[s:1302]");
        add(builder, "nq/002.gif", "[s:1301]");
        add(builder, "nq/014.gif", "[s:1300]");
        add(builder, "nq/003.gif", "[s:1299]");
        add(builder, "nq/005.gif", "[s:1298]");
        add(builder, "nq/015.gif", "[s:1297]");
        add(builder, "nq/012.gif", "[s:1296]");
        add(builder, "nq/008.gif", "[s:1295]");
        add(builder, "nq/007.gif", "[s:1294]");
        add(builder, "nq/011.jpg", "[s:1293]");
        add(builder, "nq/004.gif", "[s:1292]");
        add(builder, "nq/006.gif", "[s:1291]");
        add(builder, "nq/013.gif", "[s:1305]");
        return builder.build();
    }

    private List<Emoticon> getNormalEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "normal/058.gif", "[s:1409]");
        add(builder, "normal/026.gif", "[s:1423]");
        add(builder, "normal/110.gif", "[s:1424]");
        add(builder, "normal/077.gif", "[s:1425]");
        add(builder, "normal/101.gif", "[s:1426]");
        add(builder, "normal/052.gif", "[s:1427]");
        add(builder, "normal/108.jpg", "[s:1428]");
        add(builder, "normal/066.gif", "[s:1429]");
        add(builder, "normal/083.gif", "[s:1430]");
        add(builder, "normal/091.gif", "[s:1431]");
        add(builder, "normal/095.gif", "[s:1432]");
        add(builder, "normal/022.gif", "[s:1433]");
        add(builder, "normal/024.gif", "[s:1422]");
        add(builder, "normal/034.gif", "[s:1421]");
        add(builder, "normal/032.gif", "[s:1410]");
        add(builder, "normal/092.gif", "[s:1411]");
        add(builder, "normal/122.gif", "[s:1412]");
        add(builder, "normal/113.gif", "[s:1413]");
        add(builder, "normal/103.gif", "[s:1414]");
        add(builder, "normal/079.gif", "[s:1415]");
        add(builder, "normal/104.gif", "[s:1416]");
        add(builder, "normal/106.jpg", "[s:1417]");
        add(builder, "normal/082.gif", "[s:1418]");
        add(builder, "normal/102.gif", "[s:1419]");
        add(builder, "normal/039.gif", "[s:1420]");
        add(builder, "normal/045.jpg", "[s:1434]");
        add(builder, "normal/056.gif", "[s:1435]");
        add(builder, "normal/019.gif", "[s:1436]");
        add(builder, "normal/080.gif", "[s:1450]");
        add(builder, "normal/121.gif", "[s:1451]");
        add(builder, "normal/111.gif", "[s:1452]");
        add(builder, "normal/085.gif", "[s:1453]");
        add(builder, "normal/107.gif", "[s:1454]");
        add(builder, "normal/123.gif", "[s:1455]");
        add(builder, "normal/023.gif", "[s:1456]");
        add(builder, "normal/088.gif", "[s:1457]");
        add(builder, "normal/096.gif", "[s:1458]");
        add(builder, "normal/057.gif", "[s:1459]");
        add(builder, "normal/037.gif", "[s:1460]");
        add(builder, "normal/094.gif", "[s:1449]");
        add(builder, "normal/053.gif", "[s:1448]");
        add(builder, "normal/025.gif", "[s:1437]");
        add(builder, "normal/120.gif", "[s:1438]");
        add(builder, "normal/068.gif", "[s:1439]");
        add(builder, "normal/063.gif", "[s:1440]");
        add(builder, "normal/065.gif", "[s:1441]");
        add(builder, "normal/071.gif", "[s:1442]");
        add(builder, "normal/081.gif", "[s:1443]");
        add(builder, "normal/090.jpg", "[s:1444]");
        add(builder, "normal/117.gif", "[s:1445]");
        add(builder, "normal/049.jpg", "[s:1446]");
        add(builder, "normal/109.gif", "[s:1447]");
        add(builder, "normal/062.gif", "[s:1461]");
        add(builder, "normal/076.gif", "[s:1408]");
        add(builder, "normal/017.gif", "[s:1355]");
        add(builder, "normal/051.gif", "[s:1369]");
        add(builder, "normal/064.gif", "[s:1370]");
        add(builder, "normal/020.gif", "[s:1371]");
        add(builder, "normal/041.gif", "[s:1372]");
        add(builder, "normal/054.gif", "[s:1373]");
        add(builder, "normal/072.gif", "[s:1374]");
        add(builder, "normal/119.gif", "[s:1375]");
        add(builder, "normal/098.gif", "[s:1376]");
        add(builder, "normal/089.gif", "[s:1377]");
        add(builder, "normal/044.gif", "[s:1378]");
        add(builder, "normal/105.gif", "[s:1379]");
        add(builder, "normal/038.gif", "[s:1368]");
        add(builder, "normal/073.gif", "[s:1367]");
        add(builder, "normal/021.gif", "[s:1356]");
        add(builder, "normal/087.gif", "[s:1357]");
        add(builder, "normal/074.gif", "[s:1358]");
        add(builder, "normal/112.gif", "[s:1359]");
        add(builder, "normal/086.gif", "[s:1360]");
        add(builder, "normal/093.gif", "[s:1361]");
        add(builder, "normal/100.gif", "[s:1362]");
        add(builder, "normal/047.gif", "[s:1363]");
        add(builder, "normal/043.gif", "[s:1364]");
        add(builder, "normal/050.gif", "[s:1365]");
        add(builder, "normal/035.gif", "[s:1366]");
        add(builder, "normal/075.gif", "[s:1380]");
        add(builder, "normal/031.gif", "[s:1381]");
        add(builder, "normal/084.gif", "[s:1382]");
        add(builder, "normal/061.gif", "[s:1396]");
        add(builder, "normal/033.gif", "[s:1397]");
        add(builder, "normal/067.gif", "[s:1398]");
        add(builder, "normal/048.jpg", "[s:1399]");
        add(builder, "normal/059.gif", "[s:1400]");
        add(builder, "normal/029.gif", "[s:1401]");
        add(builder, "normal/116.gif", "[s:1402]");
        add(builder, "normal/060.gif", "[s:1403]");
        add(builder, "normal/115.jpg", "[s:1404]");
        add(builder, "normal/097.gif", "[s:1405]");
        add(builder, "normal/042.png", "[s:1406]");
        add(builder, "normal/036.gif", "[s:1395]");
        add(builder, "normal/027.gif", "[s:1394]");
        add(builder, "normal/099.gif", "[s:1383]");
        add(builder, "normal/046.gif", "[s:1384]");
        add(builder, "normal/018.gif", "[s:1385]");
        add(builder, "normal/078.gif", "[s:1386]");
        add(builder, "normal/114.gif", "[s:1387]");
        add(builder, "normal/030.gif", "[s:1388]");
        add(builder, "normal/070.gif", "[s:1389]");
        add(builder, "normal/028.gif", "[s:1390]");
        add(builder, "normal/040.gif", "[s:1391]");
        add(builder, "normal/124.gif", "[s:1392]");
        add(builder, "normal/118.gif", "[s:1393]");
        add(builder, "normal/055.gif", "[s:1407]");
        return builder.build();
    }

    private List<Emoticon> getFlashEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "flash/135.gif", "[s:1343]");
        add(builder, "flash/128.gif", "[s:1353]");
        add(builder, "flash/129.gif", "[s:1352]");
        add(builder, "flash/133.gif", "[s:1351]");
        add(builder, "flash/131.gif", "[s:1350]");
        add(builder, "flash/125.gif", "[s:1349]");
        add(builder, "flash/132.gif", "[s:1348]");
        add(builder, "flash/136.gif", "[s:1347]");
        add(builder, "flash/126.gif", "[s:1346]");
        add(builder, "flash/127.gif", "[s:1345]");
        add(builder, "flash/130.gif", "[s:1344]");
        add(builder, "flash/134.gif", "[s:1354]");
        return builder.build();
    }

    private List<Emoticon> getAnimalEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "animal/140.gif", "[s:1333]");
        add(builder, "animal/137.gif", "[s:1342]");
        add(builder, "animal/142.gif", "[s:1341]");
        add(builder, "animal/145.jpg", "[s:1340]");
        add(builder, "animal/138.gif", "[s:1339]");
        add(builder, "animal/139.gif", "[s:1338]");
        add(builder, "animal/141.gif", "[s:1337]");
        add(builder, "animal/146.gif", "[s:1336]");
        add(builder, "animal/144.gif", "[s:1335]");
        add(builder, "animal/143.gif", "[s:1334]");
        add(builder, "animal/203.gif", "[s:1462]");
        return builder.build();
    }

    private List<Emoticon> getCartonEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "carton/173.gif", "[s:1306]");
        add(builder, "carton/169.gif", "[s:1322]");
        add(builder, "carton/176.gif", "[s:1321]");
        add(builder, "carton/172.jpg", "[s:1320]");
        add(builder, "carton/179.gif", "[s:1319]");
        add(builder, "carton/174.gif", "[s:1318]");
        add(builder, "carton/168.gif", "[s:1317]");
        add(builder, "carton/167.gif", "[s:1316]");
        add(builder, "carton/178.jpg", "[s:1315]");
        add(builder, "carton/180.gif", "[s:1314]");
        add(builder, "carton/166.gif", "[s:1313]");
        add(builder, "carton/175.gif", "[s:1312]");
        add(builder, "carton/177.gif", "[s:1311]");
        add(builder, "carton/164.gif", "[s:1310]");
        add(builder, "carton/171.gif", "[s:1309]");
        add(builder, "carton/165.gif", "[s:1308]");
        add(builder, "carton/163.jpg", "[s:1307]");
        add(builder, "carton/170.gif", "[s:1323]");
        return builder.build();
    }

    private List<Emoticon> getBundamEmoticonList() {
        ImmutableList.Builder<Emoticon> builder = ImmutableList.builder();
        add(builder, "bundam/7.png", "[s:1240]");
        add(builder, "bundam/17.png", "[s:1259]");
        add(builder, "bundam/16.png", "[s:1260]");
        add(builder, "bundam/11.png", "[s:1261]");
        add(builder, "bundam/21.png", "[s:1262]");
        add(builder, "bundam/62.gif", "[s:1264]");
        add(builder, "bundam/78.gif", "[s:1265]");
        add(builder, "bundam/69.gif", "[s:1266]");
        add(builder, "bundam/73.gif", "[s:1267]");
        add(builder, "bundam/68.gif", "[s:1268]");
        add(builder, "bundam/71.gif", "[s:1269]");
        add(builder, "bundam/72.gif", "[s:1270]");
        add(builder, "bundam/63.gif", "[s:1271]");
        add(builder, "bundam/83.gif", "[s:1272]");
        add(builder, "bundam/70.gif", "[s:1273]");
        add(builder, "bundam/76.gif", "[s:1274]");
        add(builder, "bundam/22.png", "[s:1258]");
        add(builder, "bundam/10.png", "[s:1257]");
        add(builder, "bundam/3.png", "[s:1256]");
        add(builder, "bundam/65.gif", "[s:1263]");
        add(builder, "bundam/18.png", "[s:1242]");
        add(builder, "bundam/13.png", "[s:1243]");
        add(builder, "bundam/15.png", "[s:1244]");
        add(builder, "bundam/9.png", "[s:1245]");
        add(builder, "bundam/2.png", "[s:1246]");
        add(builder, "bundam/5.png", "[s:1247]");
        add(builder, "bundam/4.png", "[s:1248]");
        add(builder, "bundam/14.png", "[s:1249]");
        add(builder, "bundam/20.png", "[s:1250]");
        add(builder, "bundam/6.png", "[s:1251]");
        add(builder, "bundam/23.png", "[s:1252]");
        add(builder, "bundam/8.png", "[s:1253]");
        add(builder, "bundam/19.png", "[s:1254]");
        add(builder, "bundam/12.png", "[s:1255]");
        add(builder, "bundam/64.gif", "[s:1275]");
        return builder.build();
    }

    private void add(ImmutableList.Builder<Emoticon> builder, String emoticonFileName, String emoticonEntity) {
        builder.add(new Emoticon(ASSET_PATH_EMOTICON + emoticonFileName, emoticonEntity));
    }
}
