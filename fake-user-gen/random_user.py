from name_provider import get_rand_name
from random import randint

class RandomUser:
    def __init__(self):
        self.first_name, self.surname = get_rand_name()
        self.username = self._gen_username()
        self.email = self.username + '@test.com'
        self.password = 'password'

    def _gen_username(self):
        choice = randint(0, 4)
        if choice == 0:
            return self.first_name.lower() + '.' + self.surname.lower() + str(randint(0, 99))
        elif choice == 1:
            return self.surname.lower() + '.' + self.first_name.lower() + str(randint(0, 99))
        elif choice == 2:
            return self.first_name + self.surname + str(randint(0, 99))
        else:
            return self.surname + self.first_name + str(randint(0, 99))
            
