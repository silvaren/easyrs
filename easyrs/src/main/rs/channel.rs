#pragma version(1)
#pragma rs java_package_name(io.github.silvaren.easyrs.scripts)

uchar __attribute__((kernel)) channelR(uchar4 in, uint32_t x, uint32_t y) {
  uchar out = in.r;
  return out;
}

uchar __attribute__((kernel)) channelG(uchar4 in, uint32_t x, uint32_t y) {
  uchar out = in.g;
  return out;
}

uchar __attribute__((kernel)) channelB(uchar4 in, uint32_t x, uint32_t y) {
  uchar out = in.b;
  return out;
}