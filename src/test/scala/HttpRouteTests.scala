import akka.http.scaladsl.testkit.ScalatestRouteTest
import config.Config
import data.CsvRecord
import http.HttpRoute
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import services.ParsingService.readFromCsvFile
class HttpRouteTests extends AnyWordSpec with Matchers with ScalatestRouteTest {
  private val config = Config.load()
  private implicit val csv: Seq[CsvRecord] = readFromCsvFile(getClass.getResource(s"/${config.csv}"))
  private val route = new HttpRoute().route

  "The OilParserAPI" should {

    "return a 'Server is working now!' response for GET requests to the root path" in {
      Get("/") ~> route ~> check {
        responseAs[String] shouldEqual "Server is working now!"
      }
    }

    "return an average price be a given date for GET requests to /getPriceByDate" in {
      Get("http://localhost:8080/getPriceByDate?date=15.%D0%BC%D0%B0%D1%80.13") ~> route ~> check {
        responseAs[String] shouldEqual "764.6"
      }

      Get("http://localhost:8080/getPriceByDate?date=14.%D0%BC%D0%B0%D1%80.13") ~> route ~> check {
        responseAs[String] shouldEqual "The date is not exist"
      }

      Get("http://localhost:8080/getPriceByDate?date=14.%D0%BC%D1%80.13") ~> route ~> check {
        responseAs[String] shouldEqual "Wrong date format. It must be in the 'dd.mmm.yy' format"
      }
    }

    "return an average price between two dates for GET requests to /getAvgPriceByPeriod" in {
      Get(
        "http://localhost:8080/getAvgPriceByPeriod?from=15.%D0%BC%D0%B0%D1%80.13&to=28.%D0%BC%D0%B0%D0%B9.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual "1835.793548387097"
      }

      Get(
        "http://localhost:8080/getAvgPriceByPeriod?from=15.%D0%BC%D0%B0%D1%80.13&to=15.%D0%B0%D0%BF%D1%80.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual "789.0266666666666"
      }

      Get("" +
        "http://localhost:8080/getAvgPriceByPeriod?from=15.%D0%B0%D1%80.13&to=28.%D0%BC%D0%B0%D0%B9.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual "Wrong date format. It must be in the 'dd.mmm.yy' format"
      }

      Get("" +
        "http://localhost:8080/getAvgPriceByPeriod?from=15.%D0%B0%D1%80.13&to=28.%D0%BC%D0%B0%D0%B9.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual "Wrong date format. It must be in the 'dd.mmm.yy' format"
      }

      Get("" +
        "http://localhost:8080/getAvgPriceByPeriod?from=15.%D0%B8%D1%8E%D0%BB.13&to=28.%D0%BC%D0%B0%D0%B9.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual "Wrong date period"
      }
    }

    """return a maximum average price and a minimal average price between two dates price
      |between two dates for GET requests to /getAvgPriceByPeriod""".stripMargin in {

      Get("" +
        "http://localhost:8080/getMaxAndMinPrices?from=15.%D0%BC%D0%B0%D1%80.13&to=28.%D0%BC%D0%B0%D0%B9.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual """{
                                         |  "min_price" : "732.8",
                                         |  "max_price" : "764.6"
                                         |}""".stripMargin
      }

      Get("" +
        "http://localhost:8080/getMaxAndMinPrices?from=15.%D0%BC%D0%B0%D1%80.13&to=15.%D0%BC%D0%B0%D1%80.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual
          """{
            |  "min_price" : "764.6",
            |  "max_price" : "764.6"
            |}""".stripMargin
      }

      Get("" +
        "http://localhost:8080/getMaxAndMinPrices?from=15.%D0%BC%D1%80.13&to=15.%D0%BC%D0%B0%D1%80.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual "Wrong date format. It must be in the 'dd.mmm.yy' format"
      }

      Get("" +
        "http://localhost:8080/getMaxAndMinPrices?from=15.%D0%B8%D1%8E%D0%BD.13&to=28.%D0%BC%D0%B0%D0%B9.13"
      ) ~> route ~> check {
        responseAs[String] shouldEqual "Wrong date period"
      }
    }

    "return all csv's records for GET requests to /getStats" in {
      Get("/getStats") ~> route ~> check {
        responseAs[String] shouldEqual
          """List({
            |  "start_date" : "2013-03-15",
            |  "end_date" : "2013-04-14",
            |  "average_price" : "764.6"
            |}, {
            |  "start_date" : "2013-04-15",
            |  "end_date" : "2013-05-14",
            |  "average_price" : "732.8"
            |}, {
            |  "start_date" : "2013-05-15",
            |  "end_date" : "2013-06-14",
            |  "average_price" : "749.3"
            |}, {
            |  "start_date" : "2013-06-15",
            |  "end_date" : "2013-07-14",
            |  "average_price" : "766.9"
            |}, {
            |  "start_date" : "2013-07-15",
            |  "end_date" : "2013-08-14",
            |  "average_price" : "801.8"
            |}, {
            |  "start_date" : "2013-08-15",
            |  "end_date" : "2013-09-14",
            |  "average_price" : "827.9"
            |}, {
            |  "start_date" : "2013-09-15",
            |  "end_date" : "2013-10-14",
            |  "average_price" : "793.8"
            |}, {
            |  "start_date" : "2013-10-15",
            |  "end_date" : "2013-11-14",
            |  "average_price" : "776.8"
            |}, {
            |  "start_date" : "2013-11-15",
            |  "end_date" : "2013-12-14",
            |  "average_price" : "802.2"
            |}, {
            |  "start_date" : "2013-12-15",
            |  "end_date" : "2014-01-14",
            |  "average_price" : "787.9"
            |}, {
            |  "start_date" : "2014-01-15",
            |  "end_date" : "2014-02-14",
            |  "average_price" : "784.7"
            |}, {
            |  "start_date" : "2014-02-15",
            |  "end_date" : "2014-03-14",
            |  "average_price" : "789"
            |}, {
            |  "start_date" : "2014-03-15",
            |  "end_date" : "2014-04-14",
            |  "average_price" : "770.5"
            |}, {
            |  "start_date" : "2014-04-15",
            |  "end_date" : "2014-05-14",
            |  "average_price" : "785.6"
            |}, {
            |  "start_date" : "2014-05-15",
            |  "end_date" : "2014-06-14",
            |  "average_price" : "785.9"
            |}, {
            |  "start_date" : "2014-06-15",
            |  "end_date" : "2014-07-14",
            |  "average_price" : "791.4"
            |}, {
            |  "start_date" : "2014-07-15",
            |  "end_date" : "2014-08-14",
            |  "average_price" : "756.2"
            |}, {
            |  "start_date" : "2014-08-15",
            |  "end_date" : "2014-09-14",
            |  "average_price" : "717.4"
            |}, {
            |  "start_date" : "2014-09-15",
            |  "end_date" : "2014-10-14",
            |  "average_price" : "669.8"
            |}, {
            |  "start_date" : "2014-10-15",
            |  "end_date" : "2014-11-14",
            |  "average_price" : "603.5"
            |}, {
            |  "start_date" : "2014-11-15",
            |  "end_date" : "2014-12-14",
            |  "average_price" : "518.3"
            |}, {
            |  "start_date" : "2014-12-15",
            |  "end_date" : "2015-01-14",
            |  "average_price" : "381.8"
            |}, {
            |  "start_date" : "2015-01-15",
            |  "end_date" : "2015-02-14",
            |  "average_price" : "364.9"
            |}, {
            |  "start_date" : "2015-02-15",
            |  "end_date" : "2015-03-14",
            |  "average_price" : "424.5"
            |}, {
            |  "start_date" : "2015-03-15",
            |  "end_date" : "2015-04-14",
            |  "average_price" : "390.4"
            |}, {
            |  "start_date" : "2015-04-15",
            |  "end_date" : "2015-05-14",
            |  "average_price" : "456.9"
            |}, {
            |  "start_date" : "2015-05-15",
            |  "end_date" : "2015-06-14",
            |  "average_price" : "453.8"
            |}, {
            |  "start_date" : "2015-06-15",
            |  "end_date" : "2015-07-14",
            |  "average_price" : "430.1"
            |}, {
            |  "start_date" : "2015-07-15",
            |  "end_date" : "2015-08-14",
            |  "average_price" : "373"
            |}, {
            |  "start_date" : "2015-08-15",
            |  "end_date" : "2015-09-14",
            |  "average_price" : "330.9"
            |}, {
            |  "start_date" : "2015-09-15",
            |  "end_date" : "2015-10-14",
            |  "average_price" : "344.4"
            |}, {
            |  "start_date" : "2015-10-15",
            |  "end_date" : "2015-11-14",
            |  "average_price" : "323.6"
            |}, {
            |  "start_date" : "2015-11-15",
            |  "end_date" : "2015-12-14",
            |  "average_price" : "287.7"
            |}, {
            |  "start_date" : "2015-12-15",
            |  "end_date" : "2016-01-14",
            |  "average_price" : "237"
            |}, {
            |  "start_date" : "2016-01-15",
            |  "end_date" : "2016-02-14",
            |  "average_price" : "207.2"
            |}, {
            |  "start_date" : "2016-02-15",
            |  "end_date" : "2016-03-14",
            |  "average_price" : "243.7"
            |}, {
            |  "start_date" : "2016-03-15",
            |  "end_date" : "2016-04-14",
            |  "average_price" : "270.2"
            |}, {
            |  "start_date" : "2016-04-15",
            |  "end_date" : "2016-05-14",
            |  "average_price" : "305.1"
            |}, {
            |  "start_date" : "2016-05-15",
            |  "end_date" : "2016-06-14",
            |  "average_price" : "341.4"
            |}, {
            |  "start_date" : "2016-06-15",
            |  "end_date" : "2016-07-14",
            |  "average_price" : "327.6"
            |}, {
            |  "start_date" : "2016-07-15",
            |  "end_date" : "2016-08-14",
            |  "average_price" : "303.5"
            |}, {
            |  "start_date" : "2016-08-15",
            |  "end_date" : "2016-09-14",
            |  "average_price" : "332"
            |}, {
            |  "start_date" : "2016-09-15",
            |  "end_date" : "2016-10-14",
            |  "average_price" : "333.8"
            |}, {
            |  "start_date" : "2016-10-15",
            |  "end_date" : "2016-11-14",
            |  "average_price" : "328.4"
            |}, {
            |  "start_date" : "2016-11-15",
            |  "end_date" : "2016-12-14",
            |  "average_price" : "349.1"
            |}, {
            |  "start_date" : "2016-12-15",
            |  "end_date" : "2017-01-14",
            |  "average_price" : "383.5"
            |}, {
            |  "start_date" : "2017-01-15",
            |  "end_date" : "2017-02-14",
            |  "average_price" : "388.7"
            |}, {
            |  "start_date" : "2017-02-15",
            |  "end_date" : "2017-03-14",
            |  "average_price" : "381.5"
            |}, {
            |  "start_date" : "2017-03-15",
            |  "end_date" : "2017-04-14",
            |  "average_price" : "365.3"
            |}, {
            |  "start_date" : "2017-04-15",
            |  "end_date" : "2017-05-14",
            |  "average_price" : "351.9"
            |}, {
            |  "start_date" : "2017-05-15",
            |  "end_date" : "2017-06-14",
            |  "average_price" : "354.9"
            |}, {
            |  "start_date" : "2017-06-15",
            |  "end_date" : "2017-07-14",
            |  "average_price" : "333.3"
            |}, {
            |  "start_date" : "2017-07-15",
            |  "end_date" : "2017-08-14",
            |  "average_price" : "365.5"
            |}, {
            |  "start_date" : "2017-08-15",
            |  "end_date" : "2017-09-14",
            |  "average_price" : "378.3"
            |}, {
            |  "start_date" : "2017-09-15",
            |  "end_date" : "2017-10-14",
            |  "average_price" : "405.6"
            |}, {
            |  "start_date" : "2017-10-15",
            |  "end_date" : "2017-11-14",
            |  "average_price" : "435.2"
            |}, {
            |  "start_date" : "2017-11-15",
            |  "end_date" : "2017-12-14",
            |  "average_price" : "456.5"
            |}, {
            |  "start_date" : "2017-12-15",
            |  "end_date" : "2018-01-14",
            |  "average_price" : "485.5"
            |}, {
            |  "start_date" : "2018-01-15",
            |  "end_date" : "2018-02-14",
            |  "average_price" : "483.7"
            |}, {
            |  "start_date" : "2018-02-15",
            |  "end_date" : "2018-03-14",
            |  "average_price" : "456.6"
            |}, {
            |  "start_date" : "2018-03-15",
            |  "end_date" : "2018-04-14",
            |  "average_price" : "480.3"
            |}, {
            |  "start_date" : "2018-04-15",
            |  "end_date" : "2018-05-14",
            |  "average_price" : "524.7"
            |}, {
            |  "start_date" : "2018-05-15",
            |  "end_date" : "2018-06-14",
            |  "average_price" : "549.1"
            |}, {
            |  "start_date" : "2018-06-15",
            |  "end_date" : "2018-07-14",
            |  "average_price" : "536.8"
            |}, {
            |  "start_date" : "2018-07-15",
            |  "end_date" : "2018-08-14",
            |  "average_price" : "518.7"
            |}, {
            |  "start_date" : "2018-08-15",
            |  "end_date" : "2018-09-14",
            |  "average_price" : "543.5"
            |}, {
            |  "start_date" : "2018-09-15",
            |  "end_date" : "2018-10-14",
            |  "average_price" : "592.1"
            |}, {
            |  "start_date" : "2018-10-15",
            |  "end_date" : "2018-11-14",
            |  "average_price" : "535.8"
            |}, {
            |  "start_date" : "2018-11-15",
            |  "end_date" : "2018-12-14",
            |  "average_price" : "441.4"
            |}, {
            |  "start_date" : "2018-12-15",
            |  "end_date" : "2019-01-14",
            |  "average_price" : "408.4"
            |}, {
            |  "start_date" : "2019-01-15",
            |  "end_date" : "2019-02-14",
            |  "average_price" : "450.4"
            |}, {
            |  "start_date" : "2019-02-15",
            |  "end_date" : "2019-03-14",
            |  "average_price" : "475"
            |}, {
            |  "start_date" : "2019-03-15",
            |  "end_date" : "2019-04-14",
            |  "average_price" : "503.9"
            |}, {
            |  "start_date" : "2019-04-15",
            |  "end_date" : "2019-05-14",
            |  "average_price" : "527.3"
            |}, {
            |  "start_date" : "2019-05-15",
            |  "end_date" : "2019-06-14",
            |  "average_price" : "486.6"
            |}, {
            |  "start_date" : "2019-06-15",
            |  "end_date" : "2019-07-14",
            |  "average_price" : "462.1"
            |}, {
            |  "start_date" : "2019-07-15",
            |  "end_date" : "2019-08-14",
            |  "average_price" : "448.5"
            |}, {
            |  "start_date" : "2019-08-15",
            |  "end_date" : "2019-09-14",
            |  "average_price" : "434.5"
            |}, {
            |  "start_date" : "2019-09-15",
            |  "end_date" : "2019-10-14",
            |  "average_price" : "438.6"
            |}, {
            |  "start_date" : "2019-10-15",
            |  "end_date" : "2019-11-14",
            |  "average_price" : "447.7"
            |}, {
            |  "start_date" : "2019-11-15",
            |  "end_date" : "2019-12-14",
            |  "average_price" : "471.2"
            |}, {
            |  "start_date" : "2019-12-15",
            |  "end_date" : "2020-01-14",
            |  "average_price" : "477.6"
            |}, {
            |  "start_date" : "2020-01-15",
            |  "end_date" : "2020-02-14",
            |  "average_price" : "419.7"
            |}, {
            |  "start_date" : "2020-02-15",
            |  "end_date" : "2020-03-14",
            |  "average_price" : "345.1"
            |}, {
            |  "start_date" : "2020-03-15",
            |  "end_date" : "2020-04-14",
            |  "average_price" : "138.7"
            |}, {
            |  "start_date" : "2020-04-15",
            |  "end_date" : "2020-05-14",
            |  "average_price" : "145.3"
            |}, {
            |  "start_date" : "2020-05-15",
            |  "end_date" : "2020-06-14",
            |  "average_price" : "274.2"
            |}, {
            |  "start_date" : "2020-06-15",
            |  "end_date" : "2020-07-14",
            |  "average_price" : "319.9"
            |}, {
            |  "start_date" : "2020-07-15",
            |  "end_date" : "2020-08-14",
            |  "average_price" : "323"
            |}, {
            |  "start_date" : "2020-08-15",
            |  "end_date" : "2020-09-14",
            |  "average_price" : "312.1"
            |}, {
            |  "start_date" : "2020-09-15",
            |  "end_date" : "2020-10-14",
            |  "average_price" : "296.2"
            |}, {
            |  "start_date" : "2020-10-15",
            |  "end_date" : "2020-11-14",
            |  "average_price" : "295.1"
            |}, {
            |  "start_date" : "2020-11-15",
            |  "end_date" : "2020-12-14",
            |  "average_price" : "343.4"
            |}, {
            |  "start_date" : "2020-12-15",
            |  "end_date" : "2021-01-14",
            |  "average_price" : "377.4"
            |}, {
            |  "start_date" : "2021-01-15",
            |  "end_date" : "2021-02-14",
            |  "average_price" : "416.1"
            |}, {
            |  "start_date" : "2021-02-15",
            |  "end_date" : "2021-03-14",
            |  "average_price" : "469.7"
            |}, {
            |  "start_date" : "2021-03-15",
            |  "end_date" : "2021-04-14",
            |  "average_price" : "451.8"
            |}, {
            |  "start_date" : "2021-04-15",
            |  "end_date" : "2021-05-14",
            |  "average_price" : "477.8"
            |}, {
            |  "start_date" : "2021-05-15",
            |  "end_date" : "2021-06-14",
            |  "average_price" : "495.7"
            |}, {
            |  "start_date" : "2021-06-15",
            |  "end_date" : "2021-07-14",
            |  "average_price" : "537.5"
            |}, {
            |  "start_date" : "2021-07-15",
            |  "end_date" : "2021-08-14",
            |  "average_price" : "516"
            |}, {
            |  "start_date" : "2021-08-15",
            |  "end_date" : "2021-09-14",
            |  "average_price" : "504"
            |}, {
            |  "start_date" : "2021-09-15",
            |  "end_date" : "2021-10-14",
            |  "average_price" : "560.4"
            |}, {
            |  "start_date" : "2021-10-15",
            |  "end_date" : "2021-11-14",
            |  "average_price" : "601.9"
            |}, {
            |  "start_date" : "2021-11-15",
            |  "end_date" : "2021-12-14",
            |  "average_price" : "553"
            |}, {
            |  "start_date" : "2021-12-15",
            |  "end_date" : "2022-01-14",
            |  "average_price" : "563.3"
            |}, {
            |  "start_date" : "2022-01-15",
            |  "end_date" : "2022-02-14",
            |  "average_price" : "669.5"
            |}, {
            |  "start_date" : "2022-02-15",
            |  "end_date" : "2022-03-14",
            |  "average_price" : "697.8"
            |}, {
            |  "start_date" : "2022-03-15",
            |  "end_date" : "2022-04-14",
            |  "average_price" : "582.6"
            |}, {
            |  "start_date" : "2022-04-15",
            |  "end_date" : "2022-05-14",
            |  "average_price" : "534.6"
            |}, {
            |  "start_date" : "2022-05-15",
            |  "end_date" : "2022-06-14",
            |  "average_price" : "638.7"
            |}, {
            |  "start_date" : "2022-06-15",
            |  "end_date" : "2022-07-14",
            |  "average_price" : "616.3"
            |}, {
            |  "start_date" : "2022-07-15",
            |  "end_date" : "2022-08-14",
            |  "average_price" : "606.4"
            |}, {
            |  "start_date" : "2022-08-15",
            |  "end_date" : "2022-09-14",
            |  "average_price" : "530.4"
            |}, {
            |  "start_date" : "2022-09-15",
            |  "end_date" : "2022-10-14",
            |  "average_price" : "512.6"
            |}, {
            |  "start_date" : "2022-10-15",
            |  "end_date" : "2022-11-14",
            |  "average_price" : "519.2"
            |}, {
            |  "start_date" : "2022-11-15",
            |  "end_date" : "2022-12-14",
            |  "average_price" : "419.7"
            |}, {
            |  "start_date" : "2022-12-15",
            |  "end_date" : "2023-01-14",
            |  "average_price" : "341.8"
            |}, {
            |  "start_date" : "2023-01-15",
            |  "end_date" : "2023-02-14",
            |  "average_price" : "368.7"
            |})""".stripMargin
      }
    }
  }
}
