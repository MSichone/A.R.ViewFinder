package com.masitano.arviewfinder.utilities;

import com.masitano.arviewfinder.models.POI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masitano K.P Sichone on 7/6/2017.
 */

public class PlaceStore {

    private static PlaceStore _selfInstance = null;
    private List<POI> places;

    /**
     * Private constructor to prevent further instantiation
     */
    private PlaceStore() {
        places = new ArrayList<POI>();
        createMockPlaces();
    }
    /**
     * Factory method to get the instance of this class. This method ensures
     * that this class will have one and only one instance at any point of
     * time. This is the only way to get the instance of this class. No other
     * way will be made available to the programmer to instantiate this class.
     *
     * @return the object of this class.
     */
    public static PlaceStore getInstance() {
        if (_selfInstance == null) {
            _selfInstance = new PlaceStore();
        }
        return _selfInstance;
    }

    public void createMockPlaces(){
        POI poi = new POI("manual");
        poi.setPlaceId(1);
        poi.setLatitude(54.973833d);
        poi.setLongitude(-1.613161d);
        poi.setPlaceSource("Attraction");
        poi.setProximityAlert(true);
        poi.setPlaceName("Grey's Monument");
        poi.setAddress("");
        poi.setPlaceType("Attraction");
        poi.setOpeningHours("Always");
        poi.setPhoneNumber("011-910-1010");
        poi.setPlaceDescription("Monument to Charles Grey, 2nd Earl Grey built in 1838 in the centre of Newcastle upon Tyne, England. It was erected to acclaim Earl Grey for the passing of the Great Reform Act of 1832 and stands at the head of Grey Street. It consists of a statue of Lord Grey standing atop a 130-foot-high (40 m) column. The column was designed by local architects John and Benjamin Green, and the statue was created by the sculptor Edward Hodges Baily (creator of Nelsons statue in Trafalgar Square).");
        poi.setPlaceWebsite("https://www.newcastlegateshead.com/city-guides/greys-monument");
        addPlace(poi);

        poi = new POI("manual");
        poi.setPlaceId(2);
        poi.setLatitude(54.978954d);
        poi.setLongitude(-1.613563d);
        poi.setPlaceSource("University");
        poi.setProximityAlert(true);
        poi.setPlaceName("Kings Gate Building");
        poi.setAddress("Student Services, King's Gate, Newcastle University, Newcastle upon Tyne, NE1 7RU");
        poi.setPlaceType("University");
        poi.setOpeningHours("Mon - Fri 09:00AM - 05:00PM");
        poi.setPhoneNumber("011-910-1010");
        poi.setPlaceDescription("A dedicated  £35 million student services building at the heart of Newcastle University campus. Services housed here include Careers, Accommodation, Student Wellbeing and Student Finance");
        poi.setPlaceWebsite("http://www.ncl.ac.uk/media-files/tour/campus/kings-gate/rvtouch.html");
        addPlace(poi);

        poi = new POI("manual");
        poi.setPlaceId(3);
        poi.setLatitude(54.973614d);
        poi.setLongitude(-1.619569d);
        poi.setPlaceSource("Food");
        poi.setProximityAlert(true);
        poi.setPlaceName("Cottage Chicken");
        poi.setAddress("43 Gallowgate, Newcastle upon Tyne NE1 4SG");
        poi.setPlaceType("Food");
        poi.setOpeningHours("Mon - Fri 11:00AM - 12:00AM");
        poi.setPhoneNumber("011-910-1010");
        poi.setPlaceDescription("Late-opening, counter-serve chain for fried or grilled chicken and chips in a no-nonsense setting.");
        poi.setPlaceWebsite("http://chickencottage.com/");
        addPlace(poi);

        poi = new POI("manual");
        poi.setPlaceId(4);
        poi.setLatitude(54.975576d);
        poi.setLongitude(-1.621678d);
        poi.setPlaceSource("Attraction");
        poi.setProximityAlert(true);
        poi.setPlaceName("St. James' Park");
        poi.setAddress("Barrack Rd, Newcastle upon Tyne NE1 4ST");
        poi.setPlaceType("Attraction");
        poi.setOpeningHours("Mon - Fri 09:00AM - 08:00PM");
        poi.setPhoneNumber("0844 372 1892");
        poi.setPlaceDescription("Hilltop football stadium, home to Newcastle United Football Club and close-season rock concerts.");
        poi.setPlaceWebsite("http://nufc.co.uk/");
        addPlace(poi);

        poi = new POI("manual");
        poi.setPlaceId(5);
        //54.974317, -1.623003
        poi.setLatitude(54.974317d);
        poi.setLongitude(-1.623003d);
        poi.setPlaceSource("University");
        poi.setProximityAlert(true);
        poi.setPlaceName("Newcastle University Business School");
        poi.setAddress("5 Barrack Rd, Newcastle upon Tyne NE1 4SE");
        poi.setPlaceType("University");
        poi.setOpeningHours("Mon - Fri 9:00AM - 5:00PM");
        poi.setPhoneNumber("0191 208 1500");
        poi.setPlaceDescription("Newcastle University Business School is a hub of academic excellence and world-leading research. Learn from and collaborate with peers, experts and business leaders.");
        poi.setPlaceWebsite("http://ncl.ac.uk//business-school");
        addPlace(poi);

        //54.976498, -1.615775
        poi = new POI("manual");
        poi.setPlaceId(6);
        poi.setLatitude(54.976498d);
        poi.setLongitude(-1.615775d);
        poi.setPlaceSource("Food");
        poi.setProximityAlert(true);
        poi.setPlaceName("Subway");
        poi.setAddress("93 Percy St, Newcastle upon Tyne NE1 7RW");
        poi.setPlaceType("Food");
        poi.setOpeningHours("Mon - Fri 7:00AM - 09:00PM");
        poi.setPhoneNumber("0191 222 0108");
        poi.setPlaceDescription("Casual counter-serve chain for build-your-own sandwiches & salads.");
        poi.setPlaceWebsite("http://subway.co.uk");
        addPlace(poi);

        poi = new POI("manual");
        poi.setPlaceId(6);
        poi.setLatitude(54.974238d);
        poi.setLongitude(-1.617737d);
        poi.setPlaceSource("Food");
        poi.setProximityAlert(true);
        poi.setPlaceName("Subway");
        poi.setAddress("Magnet House,, 18-20 Gallowgate, Newcastle upon Tyne NE1 4SN");
        poi.setPlaceType("Food");
        poi.setOpeningHours("Mon - Fri 7:00AM - 11:00PM");
        poi.setPhoneNumber("0191 222 0663");
        poi.setPlaceDescription("Casual counter-serve chain for build-your-own sandwiches & salads.");
        poi.setPlaceWebsite("http://subway.co.uk");
        addPlace(poi);

        poi = new POI("manual");
        poi.setPlaceId(7);
        poi.setLatitude(54.972668d);
        poi.setLongitude(-1.623451d);
        poi.setPlaceSource("University");
        poi.setProximityAlert(true);
        poi.setPlaceName("The Key Building");
        poi.setAddress("Firebrick Avenue,, Newcastle upon Tyne");
        poi.setPlaceType("University");
        poi.setOpeningHours("Mon - Fri 9:00AM - 05:00PM");
        poi.setPhoneNumber("0191 208 6000");
        poi.setPlaceDescription("The Key is a revolutionary 'building as a lab', and is the first fabric structure to be used as a heated work space in the UK. It is the university’s first building on Science Central.");
        poi.setPlaceWebsite("http://www.ncl.ac.uk/sciencecentral/urban/thekey/");
        addPlace(poi);

        poi = new POI("manual");
        poi.setPlaceId(8);
        poi.setLatitude(54.974662d);
        poi.setLongitude(-1.620920d);
        poi.setPlaceSource("Food");
        poi.setProximityAlert(true);
        poi.setPlaceName("Nine Sports Bar & Lounge");
        poi.setAddress("St James' Park Parking, St James' Blvd, Newcastle upon Tyne NE1 4ST");
        poi.setPlaceType("Food");
        poi.setOpeningHours("Mon - Fri 10:00AM - 11:00PM");
        poi.setPhoneNumber("0191 201 8688");
        poi.setPlaceDescription("NINE Sports Bar & Lounge is located at the Gallowgate End of St. James' Park and is open throughout the week - including matchdays and non-matchdays.\n" +
                "You'll find an extensive cocktail list, your favourite beers, wines and spirits and delicious food - all in contemporary surroundings..");
        poi.setPlaceWebsite("https://www.nufc.co.uk/stadium/nine");
        addPlace(poi);

        //,
        poi = new POI("manual");
        poi.setPlaceId(9);
        poi.setLatitude(54.979080d);
        poi.setLongitude(-1.611977d);
        poi.setPlaceSource("Attraction");
        poi.setProximityAlert(true);
        poi.setPlaceName("Newcastle City Council");
        poi.setAddress("Barras Bridge, Newcastle upon Tyne NE1 8QH");
        poi.setPlaceType("Attraction");
        poi.setOpeningHours("Mon - Fri 9:00AM - 4:30PM");
        poi.setPhoneNumber("0191 278 7878");
        poi.setPlaceDescription("Newcastle City Council building.");
        poi.setPlaceWebsite("http://newcastle.gov.uk/");
        addPlace(poi);

        poi = new POI("manual");
        poi.setPlaceId(10);
        poi.setLatitude(54.980532d);
        poi.setLongitude(-1.613168d);
        poi.setPlaceSource("Attraction");
        poi.setProximityAlert(true);
        poi.setPlaceName("Great North Museum: Hancock");
        poi.setAddress("Barras Bridge, Newcastle upon Tyne NE2 4PT");
        poi.setPlaceType("Attraction");
        poi.setOpeningHours("Mon - Fri 10:00AM - 5:00PM");
        poi.setPhoneNumber("0191 208 6765");
        poi.setPlaceDescription("Visit the Great North Museum: Hancock on 19 and 20 August and you'll be greeted by riders from Hadrian's Cavalry outside on the front lawn.\n" +
                "with authentically recreated armour and saddlery the display evokes the men and their mounts who patrolled the Wall in Roman times.");
        poi.setPlaceWebsite("https://greatnorthmuseum.org.uk/");
        addPlace(poi);

    }
    public void clearPlaces(){
        setPlaces(new ArrayList<POI>());
    }

    public List<POI> getPlaces() {
        return places;
    }

    public void setPlaces(List<POI> places) {
        this.places = places;
    }

    public void addPlace(POI poi){
        this.places.add(poi);
    }

    public int getStoreSize(){
        return this.places.size();
    }

}
