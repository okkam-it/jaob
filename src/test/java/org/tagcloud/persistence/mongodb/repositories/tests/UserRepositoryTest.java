package org.tagcloud.persistence.mongodb.repositories.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tagcloud.persistence.mongodb.model.User;
import org.tagcloud.persistence.mongodb.model.UserContact;
import org.tagcloud.persistence.mongodb.model.UserDevice;
import org.tagcloud.persistence.mongodb.model.UserLocation;
import org.tagcloud.persistence.mongodb.repositories.UserDeviceRepository;
import org.tagcloud.persistence.mongodb.repositories.UserLocationRepository;
import org.tagcloud.persistence.mongodb.repositories.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springContext.xml")
public class UserRepositoryTest {


  @Autowired
  UserRepository userRepository;
  @Autowired
  UserDeviceRepository userDevicesRepository;
  @Autowired
  UserLocationRepository userLocationsRepository;
  @Autowired
  private MongoOperations mongoOperations;

  @Before
  public void cleanCollections() {

    //mongoOperations.dropCollection(User.class);
    //mongoOperations.dropCollection(UserDevice.class);
    // mongoOperations.dropCollection(UserLocation.class);
  }

  @Test
  public void createUser() {

    UserContact userContact = new UserContact();
    userContact.setGivenName("Foo");
    userContact.setFamilyName("Bar");
    userContact.setMail("foobar@gmail.com");
    userContact.setNickname("foobar");

    // store user
    User u = new User();
    u.setContact(userContact);
    u = userRepository.save(u);

    // store user location
    UserLocation currentLocation = new UserLocation(u.getId());
    currentLocation.setName("Alhambra");
    currentLocation.setLocation(34.095287000000000000,-118.127014600000000000);
    userLocationsRepository.save(currentLocation);

    // store user device
    UserDevice userdevice = new UserDevice(u.getId());
    userdevice= userDevicesRepository.save(userdevice);

    System.out.println(u);
  }

  @Test
  public void readsFirstPageCorrectly() {
    Page<User> users = userRepository.findAll(new PageRequest(0, 10));
    Assert.assertNotNull(users);
    // assertThat(users.isFirstPage(), is(true));
  }

}