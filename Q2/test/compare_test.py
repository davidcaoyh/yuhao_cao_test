import unittest

from vstrlib.vstringcomp import compare


class Test(unittest.TestCase):
    def test1(self):
        assert compare('1.2.3.4', '1.2.3.5') == -1

    def test2(self):
        assert compare('1.2.3.4', '1.2.3.4.0') == -1

    def test3(self):
        assert compare('1.2.3.6', '1.2.3.5') == 1

    def test4(self):
        self.assertRaises(Exception, compare('1.2.3.4', 'a.b.c.d'))

    def test5(self):
        assert compare('', '') == 0


if __name__ == '__main__':
    unittest.main()
