package com.example.xueya.domain.model

/**
 * 饮食方案数据模型
 * @property id 方案ID
 * @property name 方案名称
 * @property nameEn 英文名称
 * @property description 方案描述
 * @property descriptionEn 英文描述
 * @property suitableFor 适用人群
 * @property suitableForEn 适用人群（英文）
 * @property foodRecommendations 食物推荐
 * @property foodRecommendationsEn 食物推荐（英文）
 * @property precautions 注意事项
 * @property precautionsEn 注意事项（英文）
 * @property category 方案类别（主流/AI推荐）
 * @property isRecommended 是否为AI推荐
 * @property recommendationReason AI推荐理由
 * @property recommendationReasonEn AI推荐理由（英文）
 * @property isFavorite 是否已收藏
 * @property icon 图标资源（可选）
 */
data class DietPlan(
    val id: String,
    val name: String,
    val nameEn: String,
    val description: String,
    val descriptionEn: String,
    val suitableFor: String,
    val suitableForEn: String,
    val foodRecommendations: List<String>,
    val foodRecommendationsEn: List<String>,
    val precautions: String,
    val precautionsEn: String,
    val category: DietCategory,
    val isRecommended: Boolean = false,
    val recommendationReason: String = "",
    val recommendationReasonEn: String = "",
    val isFavorite: Boolean = false,
    val icon: String? = null
)

/**
 * 饮食方案类别
 */
enum class DietCategory {
    MAINSTREAM,  // 主流饮食方案
    AI_RECOMMENDED  // AI推荐方案
}

/**
 * 预定义的主流饮食方案
 */
object DietPlans {

    val mediterraneanDiet = DietPlan(
        id = "mediterranean",
        name = "地中海饮食",
        nameEn = "Mediterranean Diet",
        description = "地中海饮食以橄榄油、鱼类、坚果、全谷物、水果和蔬菜为主，有助于降低血压和改善心血管健康。",
        descriptionEn = "Mediterranean diet focuses on olive oil, fish, nuts, whole grains, fruits and vegetables, helping to lower blood pressure and improve cardiovascular health.",
        suitableFor = "高血压患者、心血管疾病预防人群",
        suitableForEn = "Hypertension patients and cardiovascular disease prevention groups",
        foodRecommendations = listOf(
            "橄榄油作为主要食用油",
            "每周至少2次鱼类摄入",
            "每天适量坚果（核桃、杏仁）",
            "大量新鲜蔬菜和水果",
            "全谷物主食",
            "适量红酒（如饮酒）"
        ),
        foodRecommendationsEn = listOf(
            "Olive oil as main cooking oil",
            "Fish at least 2 times per week",
            "Daily nuts (walnuts, almonds)",
            "Plenty of fresh fruits and vegetables",
            "Whole grain staples",
            "Moderate red wine (if drinking)"
        ),
        precautions = "控制盐分摄入，避免过度饮酒，注意坚果的热量摄入。",
        precautionsEn = "Control salt intake, avoid excessive alcohol, pay attention to nut calorie intake.",
        category = DietCategory.MAINSTREAM,
        icon = "🫒"
    )

    val dashDiet = DietPlan(
        id = "dash",
        name = "DASH饮食",
        nameEn = "DASH Diet",
        description = "DASH饮食专门设计用于降低血压，强调低钠、高钾、高钙、高镁的食物组合。",
        descriptionEn = "DASH diet is specifically designed to lower blood pressure, emphasizing low sodium, high potassium, high calcium, and high magnesium food combinations.",
        suitableFor = "高血压患者、血压偏高人群",
        suitableForEn = "Hypertension patients and people with high blood pressure",
        foodRecommendations = listOf(
            "每天8-10份蔬菜和水果",
            "低脂乳制品（每天2-3份）",
            "全谷物（每天6-8份）",
            "瘦肉、鱼类（每天2份）",
            "坚果和豆类（每周4-5份）",
            "限制钠摄入（每天<2300mg）"
        ),
        foodRecommendationsEn = listOf(
            "8-10 servings of fruits and vegetables daily",
            "Low-fat dairy products (2-3 servings daily)",
            "Whole grains (6-8 servings daily)",
            "Lean meat and fish (2 servings daily)",
            "Nuts and legumes (4-5 servings weekly)",
            "Limit sodium intake (<2300mg daily)"
        ),
        precautions = "逐渐减少钠摄入，增加钾摄入时要注意肾功能，适量增加水分摄入。",
        precautionsEn = "Gradually reduce sodium intake, pay attention to kidney function when increasing potassium, moderately increase water intake.",
        category = DietCategory.MAINSTREAM,
        icon = "🥗"
    )

    val lowSodiumDiet = DietPlan(
        id = "low_sodium",
        name = "低盐饮食",
        nameEn = "Low Sodium Diet",
        description = "严格控制钠摄入量，有助于降低血压，减少心血管疾病风险。",
        descriptionEn = "Strictly control sodium intake to help lower blood pressure and reduce cardiovascular disease risk.",
        suitableFor = "高血压患者、心血管疾病患者",
        suitableForEn = "Hypertension and cardiovascular disease patients",
        foodRecommendations = listOf(
            "选择新鲜食材，避免加工食品",
            "使用香料和香草代替盐调味",
            "选择低钠或无钠调料",
            "多吃新鲜蔬菜和水果",
            "选择未加盐的坚果",
            "阅读食品标签，选择低钠产品"
        ),
        foodRecommendationsEn = listOf(
            "Choose fresh ingredients, avoid processed foods",
            "Use spices and herbs instead of salt",
            "Choose low-sodium or no-sodium seasonings",
            "Eat more fresh fruits and vegetables",
            "Choose unsalted nuts",
            "Read food labels, choose low-sodium products"
        ),
        precautions = "钠摄入每天不超过1500mg，注意隐藏钠的来源，避免突然大幅度减少。",
        precautionsEn = "Sodium intake should not exceed 1500mg daily, pay attention to hidden sodium sources, avoid sudden large reductions.",
        category = DietCategory.MAINSTREAM,
        icon = "🧂"
    )

    val plantBasedDiet = DietPlan(
        id = "plant_based",
        name = "植物性饮食",
        nameEn = "Plant-Based Diet",
        description = "以植物性食物为主，富含纤维、钾和抗氧化剂，有助于控制血压。",
        descriptionEn = "Plant-based diet is rich in fiber, potassium and antioxidants, helping to control blood pressure.",
        suitableFor = "血压控制人群、健康意识强的人群",
        suitableForEn = "Blood pressure control groups and health-conscious people",
        foodRecommendations = listOf(
            "大量蔬菜和适量水果",
            "全谷物和豆类",
            "坚果和种子",
            "植物蛋白（豆腐、豆浆）",
            "限制动物性食物",
            "健康脂肪（橄榄油、牛油果）"
        ),
        foodRecommendationsEn = listOf(
            "Plenty of vegetables and moderate fruits",
            "Whole grains and legumes",
            "Nuts and seeds",
            "Plant proteins (tofu, soy milk)",
            "Limit animal foods",
            "Healthy fats (olive oil, avocado)"
        ),
        precautions = "注意维生素B12和铁的摄入，确保蛋白质充足，避免过度依赖加工植物食品。",
        precautionsEn = "Pay attention to vitamin B12 and iron intake, ensure adequate protein, avoid over-relying on processed plant foods.",
        category = DietCategory.MAINSTREAM,
        icon = "🌱"
    )

    /**
     * 获取所有主流饮食方案
     */
    fun getMainstreamDietPlans(): List<DietPlan> = listOf(
        mediterraneanDiet,
        dashDiet,
        lowSodiumDiet,
        plantBasedDiet
    )

    /**
     * 根据ID获取饮食方案
     */
    fun getDietPlanById(id: String): DietPlan? {
        return getMainstreamDietPlans().find { it.id == id }
    }
}