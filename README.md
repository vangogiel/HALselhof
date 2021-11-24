# HALselhof
A [HAL](http://tools.ietf.org/html/draft-kelly-json-hal) library based on [Play-JSON](https://www.playframework.com/documentation/2.3.x/ScalaJson). 

[![CI](https://github.com/vangogiel/HALselhof/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/vangogiel/HALselhof/actions/workflows/ci.yml)

## Standalone Example
```scala
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
```
[Examples](./src/test/scala/play/api/hal/HalBuilderSpec.scala)

## Play Framework Integration

```scala
// within a Play Controller HAL resources can be serialized directly and are supported within content negotiation

import play.api.hal._
import play.api.mvc.hal._

def halOrJson = Action { implicit request =>
  render {
    case Accepts.Json() => Ok(Json.obj("foo" -> "bar"))
    case AcceptHal() => Ok(
      Hal()
        .withRelation(
          "order",
          HalHref("/order")
            .withDeprecation("http://www.thisisdeprecated.com")
            .withType("application/json")
            .withHreflang("de")
            .withTemplated()
        )
        .buildJson()
    )
  }
}
```
