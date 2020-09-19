package org.liangxiong.review.server.controller;

import com.vividsolutions.jts.io.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.CoordinatesBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.liangxiong.review.server.util.WktUtil;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2019-11-22
 * @description 楼宇相关业务操作
 **/
@Slf4j
@RestController
@RequestMapping("/elastic")
public class BuildingController {


    private RestHighLevelClient client;

    /**
     * 索引名称
     */
    private static final String INDEX_NAME = "build";

    @Autowired
    public BuildingController(RestHighLevelClient client) {
        this.client = client;
    }

    @PostConstruct
    public void init() {
        // 判断索引是否存在
        boolean exists = true;
        try {
            exists = client.indices().exists(new GetIndexRequest(INDEX_NAME), RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("索引build检查失败:{}", e.getMessage());
        }
        if (!exists) {
            XContentBuilder mapping = null;
            try {
                mapping = XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject("properties")
                        // 记录id
                        .startObject("mark_location_id").field("type", "keyword").endObject()
                        // 定位信息
                        .startObject("building_geometry").field("type", "geo_shape").endObject()
                        // 楼宇id
                        .startObject("building_id").field("type", "keyword").endObject()
                        // 建筑类型
                        .startObject("building_type_name").field("type", "text").field("analyzer", "hanlp_standard").endObject()
                        // 行政区域名称
                        .startObject("administrative_region_name").field("type", "text").field("analyzer", "hanlp_standard").endObject()
                        // 楼宇名称
                        .startObject("building_name").field("type", "text").field("analyzer", "hanlp_standard").endObject()
                        .endObject().endObject();
            } catch (IOException e) {
                log.error("build索引mapping阶段构造出错:{}", e.getMessage());
            }
            // 创建索引(不能重复创建,否则报错)
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME).settings(Settings.builder()
                    // 分片数量
                    .put("index.number_of_shards", 1)
                    // 副本数量
                    .put("index.number_of_replicas", 0)).mapping(mapping);
            try {
                CreateIndexResponse response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                if (log.isInfoEnabled()) {
                    log.info(response.toString());
                }
            } catch (IOException e) {
                log.error("创建build索引失败: " + e.getMessage());
            }
        }

    }

    /**
     * 根据多边形查找匹配文档
     *
     * @param param
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @GetMapping("/house")
    public Map<String, Object> selectByPolygon(@RequestParam String param, @RequestParam int start, @RequestParam int size) throws IOException, ParseException {
        if (StringUtils.hasLength(param)) {
            List<Coordinate> coordinates = WktUtil.getCoordinates(param);
            if (!CollectionUtils.isEmpty(coordinates)) {
                PolygonBuilder builder = new PolygonBuilder(new CoordinatesBuilder().coordinates(coordinates));
                if (log.isInfoEnabled()) {
                    log.info("WKT String: {}", builder.toWKT());
                }
                GeoShapeQueryBuilder qb = QueryBuilders.geoShapeQuery(
                        "building_geometry", builder);
                qb.relation(ShapeRelation.INTERSECTS);
                SearchResponse response = client.search(new SearchRequest(new String[]{INDEX_NAME}, new SearchSourceBuilder().query(qb).sort("building_id", SortOrder.ASC).from(start).size(size)), RequestOptions.DEFAULT);
                if (response != null) {
                    List<Map<String, Object>> data = Arrays.stream(response.getHits().getHits()).map(e -> e.getSourceAsMap()).collect(Collectors.toList());
                    Map<String, Object> result = new HashedMap<>(8);
                    result.put("data", data);
                    result.put("count", response.getHits().getTotalHits().value);
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * 新增数据
     */
    @GetMapping("/add")
    public void add() {
        XContentBuilder builder;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.field("building_id", 2);
                builder.field("building_geometry", "POLYGON((6382.49186181222 -5045.43951734787, 6401.60111888351 -5062.7572763746, 6401.00395460003 -5097.39279442433, 6381.29753324526 -5114.71055344849, 6263.65616940011 -5115.9048816595, 6240.96392662794 -5087.24100465224, 6239.76959806099 -5068.13175331482, 6251.71288373055 -5045.43951734787, 6276.79378363662 -5041.85653272449, 6382.49186181222 -5045.43951734787))");
            }
            builder.endObject();
            IndexRequest request = new IndexRequest(INDEX_NAME).source(builder);
            BulkResponse response = client.bulk(new BulkRequest().add(request), RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取以指定坐标点为中心,半径为一定距离的数据
     *
     * @param latitude  维度
     * @param longitude 经度
     * @param distance  距离
     * @param start     起始位置
     * @param size      数据量
     * @return
     */
    public SearchResponse findPage(double latitude, double longitude, String distance, int start, int size) {
        // 间接实现了QueryBuilder接口
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 以某点为中心，搜索指定范围
        GeoDistanceQueryBuilder distanceQueryBuilder = new GeoDistanceQueryBuilder("location");
        distanceQueryBuilder.point(latitude, longitude);
        // 定义查询单位：公里
        distanceQueryBuilder.distance(distance, DistanceUnit.KILOMETERS);
        boolQueryBuilder.filter(distanceQueryBuilder);
        try {
            SearchResponse response = client.search(new SearchRequest(new String[]{INDEX_NAME}, new SearchSourceBuilder().query(distanceQueryBuilder).sort("id", SortOrder.ASC).from(start).size(size)), RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            log.error("search");
        }
        return null;
    }

    /**
     * 获取两点距离
     */
    @GetMapping("/distance")
    public void count() {
        // 计算两点距离,关于GeoDistance.ARC和GeoDistance.PLANE,前者比后者计算起来要慢,但精确度要比后者高
        //double distance = GeoDistance.ARC.calculate(srcLat, srcLon, dstLat, dstLon, DistanceUnit.KILOMETERS);
    }

    /**
     * 以某个坐标点为中心，按距离近远排序搜索指定范围
     *
     * @param latitude  维度
     * @param longitude 经度
     * @param distance  距离
     * @param start     起始
     * @param size      大小
     * @return
     */
    public SearchResponse findPageByCenter(double latitude, double longitude, String distance, int start, int size) {
        // 实现了SearchQuery接口，用于组装QueryBuilder和SortBuilder以及Pageable等
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 间接实现了QueryBuilder接口
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 以某点为中心，搜索指定范围
        GeoDistanceQueryBuilder distanceQueryBuilder = new GeoDistanceQueryBuilder("location");
        distanceQueryBuilder.point(latitude, longitude);
        // 定义查询单位：公里
        distanceQueryBuilder.distance(distance, DistanceUnit.KILOMETERS);
        boolQueryBuilder.filter(distanceQueryBuilder);
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        // 按距离升序
        GeoDistanceSortBuilder distanceSortBuilder =
                new GeoDistanceSortBuilder("location", latitude, longitude);
        distanceSortBuilder.unit(DistanceUnit.KILOMETERS);
        distanceSortBuilder.order(SortOrder.ASC);
        nativeSearchQueryBuilder.withSort(distanceSortBuilder);
        try {
            SearchResponse response = client.search(new SearchRequest(new String[]{INDEX_NAME}, new SearchSourceBuilder().query(nativeSearchQueryBuilder.build().getQuery()).sort("id", SortOrder.ASC).from(start).size(size)), RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
