package com.example.parcel_checker_app

import android.util.Log
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.kxml2.kdom.Element
import org.kxml2.kdom.Node

/**
 * Klasa Obiektu / singletonu, która składa się z metod służących do zgłaszania żądania
 * wyodrębnienia poszczególnych danych z odpowiedzi w postaci XML usługi sieciowej SOAP
 */

object QueryUtils {

    // stała tagu LOG
    private val LOG_TAG = QueryUtils::class.java.simpleName

    // Elementy treści koperty SOAP
    private val NAMESPACE = "http://sledzenie.pocztapolska.pl"
    private val METHOD_NAME = "sprawdzPrzesylke"

    /*  Identyfikator URI(Uniform Resource Identyfier - umożliwia łatwą identyfikacje zasobów w sieci)
        pola nagłówka akcji SOAP składający się z przestrzeni nazw i metody używanej do tworzenia odwołań
        do web service  */
    private val SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME

    /*  Adres URL Web service'u poczty polskiej (jest wykorzystywany do wyświetlania pliku WSDL
    Poprzesz dodanie ?WSDL na końcu adresu URL  */
    private val URL = "https://tt.poczta-polska.pl/Sledzenie/services/Sledzenie?WSDL"

    /**
     * SOAP Security Element
     */
    fun buildAuthHeader(): Array<Element?> {
        val headers = arrayOfNulls<Element>(1)

        // mustUnderstand
        headers[0] = Element().createElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security")
        headers[0]?.setAttribute(null, "mustUnderstand", "1")
        val security = headers[0]

        // UsernameToken
        val usernameToken = Element().createElement(security?.namespace, "UsernameToken")
        usernameToken.setAttribute("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", "UsernameToken-2")

        // Username
        val username = Element().createElement(security?.namespace, "Username")
        username.addChild(Node.TEXT, "sledzeniepp")
        usernameToken.addChild(Node.ELEMENT, username)

        // Password
        val password = Element().createElement(security?.namespace, "Password")
        password.setAttribute(null, "Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText")
        password.addChild(Node.TEXT, "PPSA")
        usernameToken.addChild(Node.ELEMENT, password)

        // Nonce
        val nonce = Element().createElement(security?.namespace, "Nonce")
        nonce.setAttribute(null, "EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary")
        nonce.addChild(Node.TEXT, "X41PkdzntfgpowZsKegMFg==")
        usernameToken.addChild(Node.ELEMENT, nonce)

        headers[0]?.addChild(Node.ELEMENT, usernameToken)

        return headers
    }

    /**
     * Funkcja pomocnicza która prosi o odpowiedź z web service, a następnie
     * zwraca ją w postaci listy zdarzeń
     */
    fun requestEventData(parcelNum: String): List<ParcelEvent>? {
        /* Prosty obiekt dynamiczny, którego można użyć do tworzenia wywołań SOAP bez implementowania KvmSerializable
           ( Jest to interfejs dostarczający metody get i set dla danych właściwości.
           Może być używany do zastąpienia odbicia (do pewnego stopnia)
           dla klas "świadomych realizacji" Obecnie używany kSOAP i opartym na repozytorium RMS obiektów kobjects) .
           Zasadniczo to jest to, co znajduje się wewnątrz ciała i wszystkie dalsze elementy podrzędne.
           Zamiast tej klasy można użyć klas niestandardowych, jeśli implementują interfejs KvmSerializable.
        */
        val request = SoapObject(NAMESPACE, METHOD_NAME)

        // Dodajemy parametr (numer przeyłki, numer wpisany przez użytkownika)
        request.addProperty("numer", parcelNum)

        // Deklarujemy wersję zapytania SOAP
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)

        // Tworzymy Nagłówek do autortyzacji
        envelope.headerOut = buildAuthHeader()

        // Można ustawić domyślną zmienną na true, żeby zapewnić zgodność z domyślnym kodowaniem dla .Net-Services
        envelope.dotNet = false

        // Przypisujemy SoapObject do koperty (envelope) jako komunikat wychodzący dla wywołania SOAP
        envelope.setOutputSoapObject(request)

        //Oparta na J2SE instancja warstwy HttpTransport adresu URL usługi sieciowej i pliku WSDL.
        val httpTransport = HttpTransportSE(URL)
        try {

            // Ta część odpowiada za wywowłanie webservice'u, ustawiając pożądany nagłówek pola nagłówka akcji SOAP
            httpTransport.call(SOAP_ACTION, envelope)

            // Sprawdź kod statusu przesyłki, jeśli jest status = 0, to pokaż listę zdarzeń
            val parcelObj = envelope.response as SoapObject
            val parcelStatusCode = if (parcelObj.propertyCount == 3) parcelObj.getProperty(2) else null

            // Jeśli status paczki = 0
            if (parcelStatusCode.toString() == "0") {
                return extractDataFromResponse(parcelObj.getProperty(0) as SoapObject)
            }
            else {
                //Toast.makeText(applicationContext, "Błedny numer", Toast.LENGTH_SHORT).show()
                return null
            }
        } catch (e: Exception) {  // Różne wyjątki
            Log.e(LOG_TAG, e.toString())
        }

        // W innym wypadku zwróć null
        return null
    }

    /**
     * Wyodrębnia dane (ParcelEvent) z odpowiedzi i zwraca jako listę zdarzeń typu ParcelEvent
     */
    @Throws(Exception::class)
    private fun extractDataFromResponse(obj: SoapObject): List<ParcelEvent>? {
        val eventList = mutableListOf<ParcelEvent>()
        val zdarzenia = obj.getProperty("zdarzenia") as SoapObject //zadarzenia

        for (i in 0 until zdarzenia.propertyCount) {
            println(i)
            val zdarzenie = zdarzenia.getProperty(i) as SoapObject
            val zdarzenieJed = zdarzenie.getProperty("jednostka") as SoapObject
            if (zdarzenie.getProperty("nazwa") != null && zdarzenie.getProperty("czas") != null && zdarzenieJed.getProperty("nazwa") != null) {
                val parcelEv = ParcelEvent(zdarzenie.getProperty("nazwa").toString(), czas = zdarzenie.getProperty("czas").toString(), nazwaJednostka = zdarzenieJed.getProperty("nazwa").toString())
                eventList.add(parcelEv)
            }

        }
        return eventList.asReversed()
    }
}

/**
 * Struktura danych dla ListView
 */
class ParcelEvent(val nazwa: String, val nazwaJednostka: String, val czas: String)