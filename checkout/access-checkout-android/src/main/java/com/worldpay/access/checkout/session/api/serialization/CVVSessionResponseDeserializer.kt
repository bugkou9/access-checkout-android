package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.session.api.SessionResponse
import com.worldpay.access.checkout.session.api.SessionResponse.Links
import org.json.JSONObject

internal class CVVSessionResponseDeserializer : Deserializer<SessionResponse>() {

    override fun deserialize(json: String): SessionResponse {
        return super.deserialize(json) {
            val root = JSONObject(json)

            val links = fetchObject(root, "_links")

            val verifiedTokenSession = fetchObject(links, "sessions:session")
            val href = toStringProperty(verifiedTokenSession, "href")

            val curiesRoot = fetchArray(links, "curies")
            val curies = curiesRoot.getJSONObject(0)
            val curiesHref = toStringProperty(curies, "href")
            val curiesName = toStringProperty(curies, "name")
            val curiesTemplated = toProperty(curies, "templated", Boolean::class)

            SessionResponse(
                Links(
                    Links.Endpoints(href),
                    arrayOf(
                        Links.Curies(curiesHref, curiesName, curiesTemplated)
                    )
                )
            )
        }
    }
}