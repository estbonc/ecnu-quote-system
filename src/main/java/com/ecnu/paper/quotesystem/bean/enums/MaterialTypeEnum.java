package com.ecnu.paper.quotesystem.bean.enums;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/5/30 10:49
 * @description
 */
public enum MaterialTypeEnum {

    // Items under category 'basic_hydropower'
    HYDROPOWER_POWER2P5("1", "hydropower_power2p5", "basic_hydropower"),
    HYDROPOWER_POWER4("1", "hydropower_power4", "basic_hydropower"),
    HYDROPOWER_POWER6("1", "hydropower_power6", "basic_hydropower"),
    HYDROPOWER_POWER10("1", "hydropower_power10", "basic_hydropower"),
    HYDROPOWER_CABLE("1", "hydropower_cable", "basic_hydropower"),
    HYDROPOWER_CABLEBOX("1", "hydropower_cableB", "basic_hydropower"),
    HYDROPOWER_PVCCABLEBOX("1", "hydropower_pvcCab", "basic_hydropower"),
    HYDROPOWER_HDTV("1", "hydropower_HD", "basic_hydropower"),
    HYDROPOWER_SOUND("1", "hydropower_sound", "basic_hydropower"),
    HYDROPOWER_WATER("1", "hydropower_water", "basic_hydropower"),
    HYDROPOWER_PPRVALVE("1", "hydropower_P", "basic_hydropower"),
    HYDROPOWER_OPENSLOT("1", "hydropower_openSl", "basic_hydropower"),
    HYDROPOWER_PUNCH("1", "hydropower_punch", "basic_hydropower"),
    HYDROPOWER_WATERDENOISE("1", "hydropower_waterDenoi", "basic_hydropower"),
    HYDROPOWER_PVCPIPE("1", "hydropower_pvcPi", "basic_hydropower"),
    HYDROPOWER_SWITCHBOX40A60A("1", "hydropower_switchbox40A", "basic_hydropower"),
    HYDROPOWER_SWITCH("1", "hydropower_switch", "basic_hydropower"),
    HYDROPOWER_WATERINLET("1", "hydropower_waterInl", "basic_hydropower"),
    HYDROPOWER_POWERSWITCH("1", "hydropower_powerSwit", "basic_hydropower"),


    // Items under category 'basic_ceiling'
    CEILING_GYPSUMLEVELING("1", "ceiling_gypsumLeveling", "basic_ceiling"),
    CEILING_INTEGYPSUMLEVELING("1", "ceiling_inteGypsumLeveling", "basic_ceiling"),
    CEILING_GYPSUMLEVELINGDIFF("1", "ceiling_gypsumLevelingDiff", "basic_ceiling"),
    CEILING_GYPSUMDOUBLEDIFF("1", "ceiling_gypsumDoubleDiff", "basic_ceiling"),
    CEILING_GYPSUMWATERTOLDIFF("1", "ceiling_gypsumWaterTolDiff", "basic_ceiling"),
    CEILING_GRIDCLOTH("1", "ceiling_gridCloth", "basic_ceiling"),
    CEILING_COREBOARDSUBSTRATE("1", "ceiling_coreboardSubstrate", "basic_ceiling"),
    CEILING_GYPSUMPELMET("1", "ceiling_gypsumPelmet", "basic_ceiling"),
    CEILING_ADDOUTLET("1", "ceiling_addOutlet", "basic_ceiling"),
    CEILING_MOLDINGSLOT("1", "ceiling_moldingSlot", "basic_ceiling"),
    CEILING_CEILINGOBLIQUE("1", "ceiling_ceilingOblique", "basic_ceiling"),
    CEILING_CEILINGVAULTED("1", "ceiling_ceilingVaulted", "basic_ceiling"),
    CEILING_CEILINGQUAQUAVERSAL("1", "ceiling_ceilingQuaquaversal", "basic_ceiling"),
    CEILING_CEILINGLINELEVELDIFF("1", "ceiling_ceilingLineLevelDiff", "basic_ceiling"),
    CEILING_CEILINGARCLEVELDIFF("1", "ceiling_ceilingArcLevelDiff", "basic_ceiling"),
    CEILING_WALLPAPER("1", "ceiling_wallpaper", "basic_ceiling"),
    CEILING_CEILINGPLAIN("1", "ceiling_ceilingPlain", "basic_ceiling"),
    CEILING_CEILINGARC("1", "ceiling_ceilingArc", "basic_ceiling"),
    CEILING_CEILINGLINE700("1", "ceiling_ceilingLine700", "basic_ceiling"),
    CEILING_CEILINGARC700("1", "ceiling_ceilingArc700", "basic_ceiling"),
    CEILING_CEILINGLINES700("1", "ceiling_ceilingLineS700", "basic_ceiling"),
    CEILING_CEILINGARCS700("1", "ceiling_ceilingArcS700", "basic_ceiling"),
    CEILING_BEAM("1", "ceiling_beam", "basic_ceiling"),
    CEILING_GYPSUMACCESSORY("1", "ceiling_gypsumAccessory", "basic_ceiling"),
    CEILING_GYPSUMMOLDING("1", "ceiling_gypsumMolding", "basic_ceiling"),
    CEILING_GYPSUMMOLDINGODD("1", "ceiling_gypsumMoldingOdd", "basic_ceiling"),
    CEILING_GYPSUMANGLELINE("1", "ceiling_gypsumAngleLine", "basic_ceiling"),
    CEILING_LAMPTROUGH("1", "ceiling_lampTrough", "basic_ceiling"),


    // Items under category 'basic_wall'
    // customized
    WALL_TILESNCS2400("1", "wall_tilesNCS2400", "basic_wall"),
    WALL_GYPSUMCHAMFER("1", "wall_gypsumChamfer", "basic_wall"),
    WALL_REINFORCEMENT("1", "wall_reinforcement", "basic_wall"),
    WALL_WINDOWFIX("1", "wall_windowFix", "basic_wall"),
    WALL_DOORFIX("1", "wall_doorFix", "basic_wall"),
    WALL_ANTICRACKING("1", "wall_antiCracking", "basic_wall"),
    WALL_WATERPROOFGYPSUM("1", "wall_waterProofGypsum", "basic_wall"),
    WALL_HANGWIREMESH("1", "wall_hangWireMesh", "basic_wall"),
    WALL_GYPSUMLEVELING("1", "wall_gypsumLeveling", "basic_wall"),
    WALL_GYPSUMLEVELINGDIFF("1", "wall_gypsumLevelingDiff", "basic_wall"),
    WALL_COREBOARDSUBSTRATE("1", "wall_coreboardSubstrate", "basic_wall"),
    WALL_GYPSUMDIFF("1", "wall_gypsumDiff", "basic_wall"),
    WALL_SURFACELEVELING("1", "wall_surfaceLeveling", "basic_wall"),
    WALL_SURFACELEVELINGDIFF("1", "wall_surfaceLevelingDiff", "basic_wall"),
    WALL_SURFACETHICKENING("1", "wall_surfaceThickening", "basic_wall"),
    WALL_WAISTDIFF("1", "wall_waistDiff", "basic_wall"),
    WALL_TILEDIFF("1", "wall_tileDiff", "basic_wall"),
    WALL_CEMENTDIFF("1", "wall_cementDiff", "basic_wall"),
    WALL_GYPSUMFANCYPANEL("1", "wall_gypsumFancyPanel", "basic_wall"),
    WALL_GRIDCLOTH("1", "wall_gridCloth", "basic_wall"),
    // automatic
    WALL_TILESCS800("1", "wall_tilesCS800", "basic_wall"),
    WALL_TILESCS2400("1", "wall_tilesCS2400", "basic_wall"),
    WALL_TILESCS400("1", "wall_tilesCS400", "basic_wall"),
    WALL_HANGMARBLE("1", "wall_hangMarble", "basic_wall"),
    WALL_GYPSUMBEAM("1", "wall_gypsumBeam", "basic_wall"),
    WALL_GYPSUMACCESSORY("1", "wall_gypsumAccessory", "basic_wall"),
    WALL_GYPSUMMOLDING("1", "wall_gypsumMolding", "basic_wall"),
    WALL_GYPSUMMOLDINGODD("1", "wall_gypsumMoldingOdd", "basic_wall"),
    WALL_SURFACEGYPSUMLEVELING("1", "wall_surfaceGypsumLeveling", "basic_wall"),
    WALL_MOSAIC("1", "wall_mosaic", "basic_wall"),
    WALL_WALLPAPER("1", "wall_wallpaper", "basic_wall"),
    WALL_MOSAICDIFF("1", "wall_mosaicDiff", "basic_wall"),
    WALL_TILESLANTDIFF("1", "wall_tileSlantDiff", "basic_wall"),
    WALL_GYPSUMPLAINFEATUREWALL("1", "wall_gypsumPlainFeatureWall", "basic_wall"),
    WALL_GYPSUMFANCYFEATUREWALL("1", "wall_gypsumFancyFeatureWall", "basic_wall"),


    // Items under category 'basic_floor'
    // customized
    FLOOR_STEELBASEBOARD("1", "floor_steelBaseboard", "basic_floor"),
    FLOOR_ELEVATING("1", "floor_elevating", "basic_floor"),
    FLOOR_LEVELINGDIFF("1", "floor_levelingDiff", "basic_floor"),
    FLOOR_LEVELINGTHINKENING10("1", "floor_levelingThinkening10", "basic_floor"),
    FLOOR_LEVELINGTHINKENING5("1", "floor_levelingThinkening5", "basic_floor"),
    FLOOR_TILEBORDERLINE("1", "floor_tileBorderLine", "basic_floor"),
    FLOOR_TILEANGLE("1", "floor_tileAngle", "basic_floor"),
    // automatic
    FLOOR_DOORSTONE("1", "floor_doorStone", "basic_floor"),
    FLOOR_MOSAIC("1", "floor_mosaic", "basic_floor"),
    FLOOR_MARBLE600("1", "floor_marble600", "basic_floor"),
    FLOOR_LEVELING("1", "floor_leveling", "basic_floor"),
    FLOOR_MOSAICPINGHUADIFF("1", "floor_mosaicPinghuaDiff", "basic_floor"),
    FLOOR_TILESLANTDIFF("1", "floor_tileSlantDiff", "basic_floor"),
    FLOOR_TILECS3200("1", "floor_tileCS3200", "basic_floor"),
    FLOOR_BARTILE("1", "floor_barTile", "basic_floor"),
    FLOOR_MARBLEBASEBOARD("1", "floor_marbleBaseboard", "basic_floor"),
    FLOOR_MARBLEPINGHUADIFF("1", "floor_marblePinghuaDiff", "basic_floor"),


    // Items under category 'basic_painting'
    // customized
    PAINTING_WATERPROOF("1", "painting_waterproof", "basic_painting"),
    // automatic
    PAINTING_WALLPAINT("1", "painting_wallpaint", "basic_painting"),


    // Items under category 'basic_setup'
    // customized
    SETUP_BATHROOMTOP("1", "setup_bathroomTop", "basic_setup"),
    SETUP_WASHBASIN("1", "setup_washBasin", "basic_setup"),
    SETUP_TOILET("1", "setup_toilet", "basic_setup"),
    SETUP_BATHTUB("1", "setup_bathtub", "basic_setup"),
    // automatic
    SETUP_LIGHT("1", "setup_light", "basic_setup"),
    SETUP_SPOTLIGHT("1", "setup_spotlight", "basic_setup"),
    SETUP_PENDANTLIGHTS500("1", "setup_pendantlightS500", "basic_setup"),
    SETUP_PENDANTLIGHTL500("1", "setup_pendantlightL500", "basic_setup"),


    // Items under category 'basic_other'
    // customized
    OTHER_GARBAGETRANSPORT("1", "other_garbageTransport", "basic_other"),
    OTHER_MATERIALCARRYDIFF("1", "other_materialCarryDiff", "basic_other"),
    OTHER_REMOTEMANAGEMENT("1", "other_remoteManagement", "basic_other"),
    OTHER_SETUPWINDOWSTONE("1", "other_setupWindowStone", "basic_other"),
    OTHER_HIGHCONSTRUCT("1", "other_highConstruct", "basic_other"),
    OTHER_HIGHCONSTRUCTDIFF("1", "other_highConstructDiff", "basic_other"),
    OTHER_HEATINGPIPEPAINTING("1", "other_heatingPipePainting", "basic_other"),
    OTHER_SETUPSAUNAPANEL("1", "other_setupSaunaPanel", "basic_other"),
    OTHER_STAIRSSUBSTRATE("1", "other_stairsSubstrate", "basic_other"),
    OTHER_SETUPCHECKVALVE("1", "other_setupCheckValve", "basic_other"),
    // automatic
    OTHER_WATERPROOF("1", "other_waterproof", "basic_other"),
    OTHER_PRODUCTPROTECTION("1", "other_productProtection", "basic_other"),
    OTHER_GARBAGECOLLECTION("1", "other_garbageCollection", "basic_other"),
    OTHER_MATERIALCARRY("1", "other_materialCarry", "basic_other"),
    OTHER_SETUPHARDWARE("1", "other_setupHardware", "basic_other"),


    // Items under category 'material'
    // customized
    DOOR_CUSTOM("2", "door_custom", "material"),
    WINDOW_CUSTOM("2", "window_custom", "material"),
    WALLPAPER_CUSTOM("2", "wallpaper_custom", "material"),
    TILES_CUSTOM("2", "tiles_custom", "material"),
    FLOOR_CUSTOM("2", "floor_custom", "material"),
    MARBLEMOSAIC_CUSTOM("2", "marblemosaic_custom", "material"),
    BEAM_CUSTOM("2", "beam_custom", "material"),
    UPHOLSTERY_CUSTOM("2", "upholstery_custom", "material"),
    CEILING_CUSTOM("2", "ceiling_custom", "material"),
    CORNICE_CUSTOM("2", "cornice_custom", "material"),
    TILESBASEBOARD_CUSTOM("2", "tilesbaseboard_custom", "material"),
    FLOORBASEBOARD_CUSTOM("2", "floorbaseboard_custom", "material"),
    DOORWINDOWPOCKET_CUSTOM("2", "doorwindowpocket_custom", "material"),
    KITCHEN_CUSTOM("2", "kitchen_custom", "material"),
    BATHROOM_CUSTOM("2", "bathroom_custom", "material"),
    HYDROPOWER_CUSTOM("2", "hydropower_custom", "material"),
    DOORSTONE_CUSTOM("2", "doorstone_custom", "material"),
    // automatic
    CEILING("2", "ceiling", "material"), // 厨卫吊顶
    WALLPAPER("2", "wallpaper", "material"), // 墙纸
    WALLPAINT("2", "wallpaint", "material"), // 墙漆
    FLOORTILES("2", "floorTiles", "material"), // 地砖
    DECORATIONTILES("2", "decorationTiles", "material"), // 花片
    WAISTLINES("2", "waistLines", "material"), // 腰线
    WALLTILES("2", "wallTiles", "material"), // 墙砖
    MARBLEMOSAIC("2", "marblemosaic", "material"), // 大理石、马赛克
    TILESBASEBOARD("2", "tilesbaseboard", "material"), // 地板踢脚线
    FLOORBASEBOARD("2", "floorbaseboard", "material"),
    FLOORBOARD("2", "floorboard", "material"), // 地板
    DOOR("2", "door", "material"), // 门
    WINDOW("2", "window", "material"), // 窗
    KITCHEN_CABINET("2", "kitchen_cabinet", "material"), // 整体橱柜
    KITCHEN("2", "kitchen", "material"), // 水盆、台面等厨房材料，按'个'计件
    KITCHEN_COOKTOP("2", "kitchen_cooktop", "material"), // 烟机、灶台
    BATHROOM("2", "bathroom", "material"), // 卫浴
    CORNICE("2", "cornice", "material"), // 石膏线
    CORNICE_ACCESSORY("2", "cornice_accessory", "material"), // 石膏饰品
    DOORSTONE("2", "doorstone", "material"), // 过门石
    DOOR_ACCESSORY("2", "door_accessory", "material"), // 门锁、门吸
    DOOR_POCKET("2", "door_pocket", "material"), // 门、窗套线
    HYDROPOWER("2", "hydropower", "material"), // 水电开关
    BEAM("2", "beam", "material"), // 梁、柱
    UPHOLSTERY("2", "upholstery", "material"), // 墙面软包


    // Items under category 'furniture'
    // customized
    FURNITURE("3", "furniture", "furniture"),
    ACCESSORY_CUSTOM("3", "accessory_custom", "furniture"),
    BED_CUSTOM("3", "bed_custom", "furniture"),
    CABINET_CUSTOM("3", "cabinet_custom", "furniture"),
    CHAIR_CUSTOM("3", "chair_custom", "furniture"),
    CURTAIN_CUSTOM("3", "curtain_custom", "furniture"),
    LIGHTING_CUSTOM("3", "lighting_custom", "furniture"),
    SOFA_CUSTOM("3", "sofa_custom", "furniture"),
    TABLE_CUSTOM("3", "table_custom", "furniture"),
    // automatic
    ACCESSORY("3", "accessory", "furniture"), // 配饰
    BED("3", "bed", "furniture"), // 床
    CABINET("3", "cabinet", "furniture"), // 柜子
    CHAIR("3", "chair", "furniture"), // 椅子
    CURTAIN("3", "curtain", "furniture"), // 窗帘
    CURTAIN_RAIL("3", "curtain_rail", "furniture"), // 窗帘杆
    CURTAIN_LOOP("3", "curtain_loop", "furniture"), // 窗幔
    CURTAIN_SCREEN("3", "curtain_screen", "furniture"), // 窗纱
    LIGHTING("3", "lighting", "furniture"), // 灯 （非厨房、卫生间）
    LIGHTBAND("3", "lightband", "furniture"), // 灯带
    BATHROOM_LIGHTING("3", "bathroom_lighting", "furniture"), // 卫生间照明
    KITCHEN_LIGHTING("3", "kitchen_lighting", "furniture"), // 厨房照明
    SOFA("3", "sofa", "furniture"), // 沙发
    TABLE("3", "table", "furniture"), // 桌子


    // Items under category 'electronics'
    // customized
    ELECTRONICS_CUSTOM("4", "electronics_custom", "electronics"),
    // automatic
    ELECTRONICS("4", "electronics", "electronics"), // 电器


    // Items under category 'other'
    // customized
    OTHER_CUSTOM("5", "other_custom", "other"),
    // automatic
    OTHER("5", "other", "other"); // 其他


    private String type;

    private String name;

    private String childType;

    MaterialTypeEnum(String type, String name, String childType) {
        this.type = type;
        this.name = name;
        this.childType = childType;
    }

    public static List<String> getCategory(List<String> typeList) {
        if(typeList == null || typeList.size() == 0){
            return Arrays.asList();
        }
        List<String> list = new ArrayList<>();
        for(String type : typeList){
            for (MaterialTypeEnum materialType : MaterialTypeEnum.values()) {
                if (materialType.type.equals(type)) {
                    list.add(materialType.getName());
                }
            }
        }
        return list;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChildType() {
        return childType;
    }

    public void setChildType(String childType) {
        this.childType = childType;
    }
}
