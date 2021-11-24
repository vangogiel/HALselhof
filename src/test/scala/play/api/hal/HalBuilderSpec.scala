package play.api.hal

import org.scalatestplus.play.PlaySpec
import play.api.hal.Hal._
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{ Json, OWrites }

class HalBuilderSpec extends PlaySpec {

  case class TestData(total: Int, currency: String, status: String)
  implicit val testWrites: OWrites[TestData] = Json.writes[TestData]

  "A HalBuilder" should {
    "build HAL resource with single relation" in {
      Hal()
        .withRelation(
          "order",
          HalHref("/order")
        )
        .build()
        .json mustBe Json.parse("""{
            "_links": {
              "order" : { 
              "href"  : "/order"
              }
           }
        }""".stripMargin)
    }

    "build json with single relation" in {
      Hal()
        .withRelation(
          "order",
          HalHref("/order")
        )
        .buildJson() mustBe Json.parse("""{
            "_links": {
              "order" : { 
              "href"  : "/order"
              }
           }
        }""".stripMargin)
    }

    "build json with single relation and optional link attributes" in {
      Hal()
        .withRelation(
          "order",
          HalHref("/order")
            .withDeprecation("http://www.thisisdeprecated.com")
            .withType("application/json")
            .withHreflang("de")
            .withTemplated()
        )
        .buildJson() mustBe Json.parse("""{
            "_links": {
              "order" : { 
                "href"  : "/order",
                "deprecation": "http://www.thisisdeprecated.com",
                "type": "application/json",
                "hreflang": "de",
                "templated": true
              }
           }
        }""".stripMargin)
    }

    "build json with multiple single relations" in {
      Hal()
        .withRelation(
          "self",
          HalHref("/orders")
        )
        .withRelation(
          "next",
          HalHref("/orders?page=2")
        )
        .withRelation(
          "find",
          HalHref("/orders{?id}")
            .withTemplated()
        )
        .buildJson() mustBe Json.parse("""{
            "_links": {
                "self": { "href": "/orders" },
                "next": { "href": "/orders?page=2" },
                "find": { "href": "/orders{?id}", "templated": true }
           }
        }""".stripMargin)
    }

    "build json with multiple relations" in {
      Hal()
        .withRelation(
          "orders",
          Seq(
            HalHref(href = "/order/1").withName("1").withTemplated(),
            HalHref("/order/2").withName("2").withTemplated(),
            HalHref("/order/3").withName("3").withTemplated()
          )
        )
        .buildJson() mustBe Json.parse("""{
          "_links": {
            "orders" : [
              { "href": "/order/1", "name": "1", "templated": true},
              { "href": "/order/2", "name": "2", "templated": true},
              { "href": "/order/3", "name": "3", "templated": true}
          ]}
        }""".stripMargin)
    }

    "build json with relations and custom data" in {
      Hal()
        .withRelation(
          "self",
          HalHref("/orders")
        )
        .withRelation(
          "next",
          HalHref("/orders?page=2")
        )
        .withRelation(
          "find",
          HalHref("/orders{?id}")
            .withTemplated()
        )
        .withCustomData(TestData(20, "EUR", "shipped"))
        .buildJson() mustBe Json.parse("""{
          "_links": {
                "self": { "href": "/orders" },
                "next": { "href": "/orders?page=2" },
                "find": { "href": "/orders{?id}", "templated": true }
          },
          "total" : 20,
          "currency" : "EUR",
          "status": "shipped"
        }""".stripMargin)
    }

    "build json with multiple links in a relation and custom data" in {
      Hal()
        .withRelation(
          "self",
          HalHref("/orders")
        )
        .withRelation(
          "next",
          HalHref("/orders?page=2")
        )
        .withRelation(
          "find",
          HalHref("/orders{?id}")
            .withTemplated()
        )
        .withRelation(
          "curies",
          Seq(
            HalHref("/order/1").withName("1").withTemplated(),
            HalHref("/order/2").withName("2").withTemplated(),
            HalHref("/order/3").withName("3").withTemplated()
          )
        )
        .withCustomData(TestData(20, "EUR", "shipped"))
        .buildJson() mustBe Json.parse("""{
          "_links": {
              "self": { "href": "/orders" },
              "next": { "href": "/orders?page=2" },
              "find": { "href": "/orders{?id}", "templated": true },
              "curies" : [
                  { "href": "/order/1", "name": "1", "templated": true},
                  { "href": "/order/2", "name": "2", "templated": true},
                  { "href": "/order/3", "name": "3", "templated": true}
          ]},
          "total" : 20,
          "currency" : "EUR",
          "status": "shipped"
        }""".stripMargin)
    }

    "build json with multiple links in a relation, embedded and custom data" in {
      Hal()
        .withRelation("self", HalHref("/orders"))
        .withRelation("next", HalHref("/orders?page=2"))
        .withRelation("find", HalHref("/orders{?id}").withTemplated())
        .withRelation(
          "curies",
          Seq(
            HalHref("/order/1").withName("1").withTemplated(),
            HalHref("/order/2").withName("2").withTemplated(),
            HalHref("/order/3").withName("3").withTemplated()
          )
        )
        .withEmbedded(
          "orders",
          TestData(30, "USD", "shipped").asResource ++
            HalRelation("self", "/orders/123") ++
            HalRelation("basket", "/baskets/98712") ++
            HalRelation("customer", "/customers/7809"),
          TestData(20, "USD", "processing").asResource ++
            HalRelation("self", "/orders/124") ++
            HalRelation("basket", "/baskets/97213") ++
            HalRelation("customer", "/customers/12369")
        )
        .withCustomData(TestData(20, "EUR", "shipped"))
        .buildJson() mustBe Json.parse("""{
          "_links": {
              "self": { "href": "/orders" },
              "next": { "href": "/orders?page=2" },
              "find": { "href": "/orders{?id}", "templated": true },
              "curies" : [
                  { "href": "/order/1", "name": "1", "templated": true},
                  { "href": "/order/2", "name": "2", "templated": true},
                  { "href": "/order/3", "name": "3", "templated": true}
          ]},
          "_embedded": {
            "orders": [{
                "_links": {
                  "self": { "href": "/orders/123" },
                  "basket": { "href": "/baskets/98712" },
                  "customer": { "href": "/customers/7809" }
                },
                "total": 30,
                "currency": "USD",
                "status": "shipped"
              },{
                "_links": {
                  "self": { "href": "/orders/124" },
                  "basket": { "href": "/baskets/97213" },
                  "customer": { "href": "/customers/12369" }
                },
                "total": 20,
                "currency": "USD",
                "status": "processing"
            }]
          },
          "total" : 20,
          "currency" : "EUR",
          "status": "shipped"
        }""".stripMargin)
    }
  }
}
