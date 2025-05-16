# -*- coding: utf-8 -*-
import base64
from Crypto.Cipher import AES

IV = "1236547899874560"
class aes_utils(object):
    def __init__(self):
        self.mode = AES.MODE_CBC

    '''
    获取加密密钥Base64编码
    '''
    def generate_key(self, key_bytes):
        return base64.b64encode(key_bytes)

    def __get_secret_key(self,base64_key_str):
        return base64.b64decode(base64_key_str)

    def __padding(self, key_bytes):
        BS = len(key_bytes)
        self.pad = lambda s: s + (BS - len(s.encode('utf8')) % BS) * chr(BS - len(s.encode('utf8')) % BS)
        self.unpad = lambda s: s[0:-ord(s[-1:])]


    '''
    AES加密
    '''
    def encrypt(self, text, base64_key):
        secret_key = self.__get_secret_key(base64_key)
        self.__padding(secret_key)
        encryptor = AES.new(secret_key, self.mode, IV.encode("utf8"))
        pad_str = self.pad(text)
        self.ciphertext = encryptor.encrypt(bytes(pad_str, encoding="utf8"))
        return base64.b64encode(self.ciphertext)

    '''
    AES解密
    '''
    def decrypt(self, text, base64_key):
        secret_key = self.__get_secret_key(base64_key)
        self.__padding(secret_key)
        decode = base64.b64decode(text)
        decryptor = AES.new(secret_key, self.mode, IV.encode("utf8"))
        plain_text = decryptor.decrypt(decode)
        return str(self.unpad(plain_text),encoding='utf8')
