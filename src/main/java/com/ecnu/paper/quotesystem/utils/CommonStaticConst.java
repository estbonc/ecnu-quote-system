package com.ecnu.paper.quotesystem.utils;


public class CommonStaticConst {

    private CommonStaticConst() {
    }

    /**
     * 通用常量
     */
    public static class Common {
        private Common() {
        }

        /**
         * 有效状态映射
         */
        public static final String STATUS = "status";

        /**
         * 有效状态
         */
        public static final Integer ENABLE_STATUS = 1;

        /**
         * 无效状态
         */
        public static final Integer DISABLE_STATUS = 0;

        /**
         * 分页limit
         */
        public static final String LIMIT = "limit";

        /**
         * 分页offset
         */
        public static final String OFFSET = "offset";

        /**
         * 序列化ID
         */
        public static final String SERIALV_VERSION_UID = "serialVersionUID";

        public static final Integer DEFAULT_OFFSET = 1;
        public static final Integer DEFAULT_LIMIT = 15;
        public static final Long TIME_FOR_ONE_DAY = 86400000l;
        public static final Integer BATCH_TEMPLATE_FILED_NUM = 9;
        /**
         * 创建时间
         */
        public static final String CREATE_TIME = "createTime";
        /**
         * 更新时间
         */
        public static final String UPDATE_TIME = "updateTime";
        /**
         * 更新人
         */
        public static final String UPDATE_BY = "updateBy";
        /**
         * 默认装饰公司
         */
        public static final String DEFAULT_DECORATION_COMPANY = "设计创客";

    }

    /**
     * 施工项相关
     */
    public static class Construct {
        private Construct() {
        }

        /**
         * 空间带出施工项
         */
        public static final String TYPE_ROOM = "room";

        /**
         * 主材带出施工项
         */
        public static final String TYPE_MATERIAL = "material";

        /**
         * 施工项ID
         */
        public static final String CONSTRUCT_ID = "constructId";

        /**
         * 报价查询套餐内施工项Type
         */
        public static final Integer QUERY_CONSTRUCT_PKG_INSIDE = 1;

        /**
         * 报价查询大礼包施工项Type
         */
        public static final Integer QUERY_CONSTRUCT_BAG = 2;
        /**
         * 报价查询套餐外施工项Type
         */
        public static final Integer QUERY_CONSTRUCT_PKG_OUTSIDE = 3;

        /**
         * 施工项名称
         */
        public static final String CONSTRUCT_NAME = "constructName";
        /**
         * 施工项编码
         */
        public static final String CONSTRUCT_CODE = "constructCode";
        /**
         * 施工项分类
         */
        public static final String CONSTRUCT_ITEM = "constructItem";
        /**
         * 施工项类别
         */
        public static final String CONSTRUCT_CATEGORY = "constructCategory";
        /**
         * 装饰公司
         */
        public static final String DECORATE_COMPANY = "decorationCompany";
        /**
         * 单位
         */
        public static final String UNIT_CODE = "unitCode";
        /**
         * 计量单位
         */
        public static final String DESC = "desc";
        /**
         * 辅料名称规格
         */
        public static final String ASSIT_SPEC = "assitSpec";
        /**
         * 验收标准
         */
        public static final String STANDARD = "standard";
        /**
         * 标准出处
         */
        public static final String SOURCE_OF_STANDARD = "sourceOfStandard";
        /**
         * 备注
         */
        public static final String REMARK = "remark";
        /**
         * 状态
         */
        public static final String STATUS = "status";
        public static final String BATCH_NUMBER = "batchNum";
        public static final String LOG_STATUS = "logStatus";
        public static final String BATCH_RESULT_ID = "batchResultId";
        public static final String RESULT = "result";

        /**
         * 有效状态
         */
        public static final Integer BINDING = 1;

        /**
         * 无效状态
         */
        public static final Integer NOT_BINDING = 0;

        //施工项是否主材默认带出
        /**
         * 默认带出
         */
        public static final Integer DEFAULT = 1;

        /**
         * 非默认带出
         */
        public static final Integer NOT_DEFAULT = 0;

        /**
         * 装饰公司对应施工项
         */
        public static final String DEFAULT_CONSTRUCT_CATEGORY = "自定义项目";
    }

    /**
     * 套餐相关
     */
    public static class Package {
        private Package() {
        }

        /**
         * 套餐ID
         */
        public static final String PACKAGE_ID = "packageId";

        /**
         * 套餐版本ID
         */
        public static final String PACKAGE_VERSION_ID = "packageVersionId";

        /**
         * 房型
         */
        public static final String HOUSE_TYPE = "houseType";
        /**
         * 空间类型
         */
        public static final String ROOM_TYPE = "roomType";
        /**
         * 版本
         */
        public static final String VERSION = "version";

        /**
         * 套餐空间id
         */
        public static final String PKG_ROOM_ID = "packageRoomId";
        public static final String PKG_PRICE_ID = "packagePriceId";
        public static final String PKG_BAG_ID = "bagId";
        public static final String PKG_CONSTRUCT_ID = "packageConstructId";
        public static final String PACKAGE_ROOM_ID = "packageRoomId";
        public static final String MATERIAL_CODE = "materialCode";
        public static final String BRANDS = "brands";
        public static final String MATERIAL_ID = "materialId";
        public static final String BRANDS_BRANDCODE = "brands.brandCode";
        public static final String BRANDS_PRODUCT_PCODE = "brands.product.pcode";
        public static final String CONSTRUCT_ID = "constructId";
        public static final String SELECTED_CONSTRUCT_ID = "selectedConstruct.cid";
        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";
        public static final String REGION = "region";
        public static final String STORE = "store";
    }

    public static class Dictionary {
        private Dictionary() {
        }

        /**
         * 类型
         */
        public static final String TYPE = "type";
        /**
         * 编码
         */
        public static final String CODE = "code";
        /**
         * 优先级
         */
        public static final String PRIORITY = "priority";


        /**
         * -------------------type--------------------------
         */
        /**
         * 单位
         */
        public static final String TYPE_UNIT = "unit";
        /**
         * 施工项类别
         */
        public static final String TYPE_CONSTRUCT_CATEGORY = "construct_category";
        /**
         * 施工项分类
         */
        public static final String TYPE_CONSTRUCT_ITEM = "construct_item";

    }

    public static class HouseRoom {
        private HouseRoom() {
        }

        /**
         * 空间id
         */
        public static final String ROOM_ID = "roomId";
        /**
         * 房型id
         */
        public static final String HOUSE_TYPE_ID = "houseTypeId";
        /**
         * 包含的空间集合
         */
        public static final String ROOMS = "rooms";
        /**
         * 备注
         */
        public static final String REMARK = "remark";
        /**
         * 装饰公司
         */
        public static final String DECORATION_COMPANY = "decorationCompany";
    }

}
