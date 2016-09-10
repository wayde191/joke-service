(ns joke-service.config)

(defn- env [key] (System/getenv key))

(defn east-fund-service-url []
  (env "EAST_FUND_SERVICE_URL"))

(def fund-code-url "http://fund.eastmoney.com/js/fundcode_search.js")

(def fund-company-url "http://fund.eastmoney.com/api/static/FundCommpanyInfo.js")

(def east-fund-url "http://fund.eastmoney.com/")

(defn get-funds-data-url [date]
  (str "http://fund.eastmoney.com/Data/Fund_JJJZ_Data.aspx?t=1&lx=1&letter=&gsid=&text=&sort=zdf,desc&page=1,9999&feature=|&dt="
    date
    "&atfc=&onlySale=0"))

(defn get-fund-history-url [code page]
  (str "http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code=" code
    "&page=" page
    "&per=200"))

; Start here
(defn get-joke-url [page timestamp]
  (str "http://interface.sina.cn/ent/feed.d.json?ch=joke&col=joke&show_num=20&page=" page
    "&act=more&jsoncallback=callbackFunction&_=" timestamp
    "&callback=jsonp1"))


